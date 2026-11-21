package meknuc.reactor.spentpool;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.util.WorldUtils;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolPort;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 乏燃料冷却池数据。
 * <p>
 * 冷却逻辑：
 * <ul>
 *     <li>并行冷却线程数上限 = 多方块体积，每个线程同时处理一根“枯竭的热”燃料棒；</li>
 *     <li>每根燃料棒默认需要 240 秒（4800 tick）完成冷却；</li>
 *     <li>每个线程每 tick 消耗 1mb 水并产出 1mb 蒸汽；</li>
 *     <li>输入缓存最多 64 根热燃料棒，支持不同种类混合缓存、各自独立并行冷却；</li>
 *     <li>线程已满时暂停接收新燃料棒（手持与管道都拒绝），等有空闲线程再恢复输入；</li>
 *     <li>冷却完成缓存最多 64 根，按种类分别记入输出槽；</li>
 *     <li>完成缓存已满时暂停所有线程进度，直到缓存被输出腾出空间；</li>
 *     <li>无水或蒸汽罐满时暂停所有冷却进度；</li>
 *     <li>冷却运行时以随机间隔播放盖革计数器咔嗒声，模拟高辐射环境下的随机计数；</li>
 * </ul>
 * 端口按模式自动工作：
 * <ul>
 *     <li>输出蒸汽：向相邻的化学品容器/加压管道自动输出蒸汽；</li>
 *     <li>输入乏燃料：通过物品能力或手持放入“枯竭的热”燃料棒；</li>
 *     <li>输出废料：把内部冷却完成的燃料棒自动输出到相邻物品容器；</li>
 *     <li>输入水：从相邻流体容器抽取水。</li>
 * </ul>
 */
public class MeknucSpentFuelPoolMultiblockData extends MultiblockData {

    @ContainerSync
    public IExtendedFluidTank waterTank;
    @ContainerSync
    public IChemicalTank steamTank;
    private final List<IInventorySlot> inputSlots = new ArrayList<>(MeknucSpentFuelPoolConstants.INPUT_SLOT_COUNT);
    private final List<IInventorySlot> outputSlots = new ArrayList<>(MeknucSpentFuelPoolConstants.MAX_COOLED_RODS);
    private final MeknucSpentFuelPoolProcess process;

    private int waterCapacity;
    private int steamCapacity;

    @ContainerSync(getter = "getMaxThreads")
    public int clientMaxThreads;
    @ContainerSync(getter = "getRunningThreads")
    public int clientRunningThreads;
    @ContainerSync(getter = "getCooledCount")
    public int clientCooledCount;

    public MeknucSpentFuelPoolMultiblockData(net.minecraft.world.level.block.entity.BlockEntity tile) {
        super(tile);
        IContentsListener saveAndComparator = createSaveAndComparator();
        this.waterTank = VariableCapacityFluidTank.input(this, this::getWaterCapacity,
              stack -> stack.getFluid() == Fluids.WATER, saveAndComparator);
        this.steamTank = VariableCapacityChemicalTank.output(this, this::getSteamCapacity,
              stack -> stack.getChemical() == MekanismChemicals.STEAM.value(), saveAndComparator);
        this.fluidTanks.add(this.waterTank);
        this.chemicalTanks.add(this.steamTank);

        for (int i = 0; i < MeknucSpentFuelPoolConstants.INPUT_SLOT_COUNT; i++) {
            // 线程已满时不再接收新燃料棒，等有空闲线程再放开输入，避免物品滞留在看不见的缓存里
            IInventorySlot slot = BasicInventorySlot.at(
                  (stack, automationType) -> automationType == AutomationType.INTERNAL,
                  (stack, automationType) -> hasFreeThread() && isAcceptedFuel(stack),
                  MeknucSpentFuelPoolMultiblockData::isAcceptedFuel, saveAndComparator, -1000, -1000);
            this.inputSlots.add(slot);
            this.inventorySlots.add(slot);
        }

        for (int i = 0; i < MeknucSpentFuelPoolConstants.MAX_COOLED_RODS; i++) {
            // 输出槽相反：任何人都能取走冷却完成的燃料棒，但不能再放回东西。
            IInventorySlot slot = BasicInventorySlot.at(
                  (stack, automationType) -> true,
                  (stack, automationType) -> false,
                  MeknucSpentFuelPoolMultiblockData::isCooledRod, saveAndComparator, -1000, -1000);
            this.outputSlots.add(slot);
            this.inventorySlots.add(slot);
        }
        this.process = new MeknucSpentFuelPoolProcess(this, inputSlots, outputSlots);
    }

    /**
     * 仅接受“枯竭的热”燃料棒：同一 {@code meknuc:fuel_rod} 物品 + 组件
     * （radiation=0 且 rod_hot=true），不依赖语言文件里的显示名称。
     */
    public static boolean isAcceptedFuel(ItemStack stack) {
        return stack.getItem() instanceof MeknucItemFuelRodBase
              && MeknucItemFuelRodBase.isDepleted(stack)
              && MeknucItemFuelRodBase.isHot(stack);
    }

    public static boolean isCooledRod(ItemStack stack) {
        return stack.getItem() instanceof MeknucItemFuelRodBase
              && MeknucItemFuelRodBase.isDepleted(stack)
              && !MeknucItemFuelRodBase.isHot(stack);
    }

    @Override
    public void setVolume(int volume) {
        if (getVolume() != volume) {
            super.setVolume(volume);
            this.waterCapacity = volume * MeknucSpentFuelPoolConstants.FLUID_PER_VOLUME;
            this.steamCapacity = volume * MeknucSpentFuelPoolConstants.FLUID_PER_VOLUME;
        }
    }

    public int getWaterCapacity() {
        return waterCapacity;
    }

    public long getSteamCapacity() {
        return steamCapacity;
    }

    public IExtendedFluidTank getWaterTank() {
        return waterTank;
    }

    public IChemicalTank getSteamTank() {
        return steamTank;
    }

    public List<IInventorySlot> getInputSlots() {
        return inputSlots;
    }

    /**
     * 当前数据持有的全部槽位数量（输入 + 输出），供旧存档缓存迁移时比对长度。
     * 注意不能用 {@code getInventorySlots(null)}：未成型时服务端会返回空列表。
     */
    public int getSlotCount() {
        return inventorySlots.size();
    }

    public List<IInventorySlot> getOutputSlots() {
        return outputSlots;
    }

    public List<RodThread> getThreads() {
        return process.getThreads();
    }

    /**
     * 把热燃料棒塞入输入缓存，返回剩余物品。
     */
    public ItemStack insertFuel(ItemStack stack, Action action) {
        ItemStack remainder = stack;
        for (IInventorySlot slot : inputSlots) {
            if (remainder.isEmpty()) {
                break;
            }
            remainder = slot.insertItem(remainder, action, AutomationType.MANUAL);
        }
        return remainder;
    }

    public int getInputCount() {
        int total = 0;
        for (IInventorySlot slot : inputSlots) {
            total += slot.getStack().getCount();
        }
        return total;
    }

    public int getRunningThreads() {
        return isRemote() ? clientRunningThreads : process.size();
    }

    public int getCooledCount() {
        return isRemote() ? clientCooledCount : countCooledRods();
    }

    private int countCooledRods() {
        int total = 0;
        for (IInventorySlot slot : outputSlots) {
            if (!slot.isEmpty()) {
                total += slot.getStack().getCount();
            }
        }
        return total;
    }

    /**
     * 并行线程上限 = 多方块体积。
     */
    public int getMaxThreads() {
        return isRemote() ? clientMaxThreads : getVolume();
    }

    /**
     * 是否还有空闲的冷却线程可以立刻接手新的热燃料棒。
     * 线程已满时暂停输入（手持与管道都拒绝），等有线程空出来再恢复，
     * 避免燃料棒被塞进看不见的输入缓存里。
     */
    public boolean hasFreeThread() {
        return isRemote() || process.size() < getMaxThreads();
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (!isFormed()) {
            return needsPacket;
        }
        handleValveIO(world);
        int cooled = getCooledCount();
        boolean paused = cooled >= MeknucSpentFuelPoolConstants.MAX_COOLED_RODS
              || waterTank.getFluidAmount() <= 0
              || steamTank.getStored() >= steamTank.getCapacity();
        process.tick(world, paused, cooled);
        return needsPacket;
    }

    /**
     * 端口自动输入/输出。
     */
    private void handleValveIO(Level world) {
        for (IValveHandler.ValveData valve : valves) {
            if (!(WorldUtils.getTileEntity(world, valve.location) instanceof TileEntitySpentFuelPoolPort port)) {
                continue;
            }
            BlockPos target = valve.location.relative(valve.side);
            Direction side = valve.side.getOpposite();
            switch (port.getPortMode()) {
                case COOLANT_OUTPUT -> pushSteam(world, target, side);
                case WASTE_OUTPUT -> pushWaste(world, target, side);
                case COOLANT_INPUT -> pullWater(world, target, side);
                default -> {
                }
            }
        }
    }

    private void pushSteam(Level world, BlockPos target, Direction side) {
        if (steamTank.getStored() <= 0) {
            return;
        }
        IChemicalHandler handler = world.getCapability(Capabilities.CHEMICAL.block(), target, side);
        if (handler == null) {
            return;
        }
        ChemicalStack stack = steamTank.getStack().copy();
        ChemicalStack remainder = handler.insertChemical(stack, Action.EXECUTE);
        long moved = stack.getAmount() - remainder.getAmount();
        if (moved > 0) {
            steamTank.extract(moved, Action.EXECUTE, AutomationType.INTERNAL);
        }
    }

    private void pushWaste(Level world, BlockPos target, Direction side) {
        IItemHandler handler = world.getCapability(Capabilities.ITEM.block(), target, side);
        if (handler == null) {
            return;
        }
        for (IInventorySlot slot : outputSlots) {
            if (slot.isEmpty()) {
                continue;
            }
            ItemStack stack = slot.getStack();
            for (int i = 0; i < handler.getSlots() && !stack.isEmpty(); i++) {
                stack = handler.insertItem(i, stack, false);
            }
            slot.setStack(stack);
            if (!slot.isEmpty()) {
                return;
            }
        }
    }

    private void pullWater(Level world, BlockPos target, Direction side) {
        int needed = waterTank.getNeeded();
        if (needed <= 0) {
            return;
        }
        IFluidHandler handler = world.getCapability(Capabilities.FLUID.block(), target, side);
        if (handler == null) {
            return;
        }
        FluidStack drain = handler.drain(Math.min(needed, 1_000), IFluidHandler.FluidAction.SIMULATE);
        if (drain.isEmpty() || drain.getFluid() != Fluids.WATER) {
            return;
        }
        FluidStack drained = handler.drain(drain, IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
            waterTank.insert(drained, Action.EXECUTE, AutomationType.INTERNAL);
        }
    }

    public List<RodThread> copyThreads() {
        return process.copyThreads();
    }

    public void restoreThreads(List<RodThread> restored) {
        process.restoreThreads(restored);
    }

    void markChanged() {
        markDirty();
    }

    /**
     * 单根燃料棒的冷却线程。
     */
    public static final class RodThread {

        private final MeknucRodType type;
        int progress;

        public RodThread(MeknucRodType type, int progress) {
            this.type = type;
            this.progress = progress;
        }

        public MeknucRodType getType() {
            return type;
        }

        public int getProgress() {
            return progress;
        }

        @Override
        public String toString() {
            return "RodThread[" + type + ", " + progress + "/" + MeknucSpentFuelPoolConstants.COOLING_TICKS + "]";
        }
    }
}
