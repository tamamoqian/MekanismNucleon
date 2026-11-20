package meknuc.reactor.spentpool;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.sounds.SoundSource;
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

    /** 单根燃料棒的冷却时间（tick），240 秒。 */
    public static final int COOLING_TICKS = 240 * 20;
    /** 输入缓存最多 64 根热燃料棒。 */
    public static final int MAX_INPUT_RODS = 64;
    /** 冷却完成燃料棒缓存上限。 */
    public static final int MAX_COOLED_RODS = 64;
    /**
     * 每个输入槽只放 1 根。燃料棒是同一物品 + 不同组件（rod_type/rod_hot），
     * 不同种类之间无法叠放进同一个槽，因此“一槽一根”才能让任意种类组合同时进入缓存，
     * 而不是被输入槽数量限制住可同时处理的燃料棒种类数。
     */
    public static final int INPUT_SLOT_LIMIT = 1;
    public static final int INPUT_SLOT_COUNT = MAX_INPUT_RODS / INPUT_SLOT_LIMIT;
    /** 每方格体积对应的流体储量（水/蒸汽）。 */
    public static final int FLUID_PER_VOLUME = 1_000;
    /** 每个线程每 tick 的水消耗与蒸汽产出。 */
    public static final int WATER_PER_THREAD_PER_TICK = 1;
    public static final int STEAM_PER_THREAD_PER_TICK = 1;
    /** 运行音效最小间隔（tick），避免随机到连续两 tick 叠成“机枪”。 */
    public static final int MIN_CLICK_INTERVAL_TICKS = 2;
    /** 在最小间隔上再随机 0..N tick：模拟高辐射环境下盖革计数器不规则的咔嗒声。 */
    public static final int RANDOM_CLICK_EXTRA_TICKS = 7;

    @ContainerSync
    public IExtendedFluidTank waterTank;
    @ContainerSync
    public IChemicalTank steamTank;
    private final List<IInventorySlot> inputSlots = new ArrayList<>(INPUT_SLOT_COUNT);
    private final List<IInventorySlot> outputSlots = new ArrayList<>(MAX_COOLED_RODS);
    private final List<RodThread> threads = new ArrayList<>();

    private int waterCapacity;
    private int steamCapacity;
    private int dirtyCounter;
    /** 盖革计数器运行音效的剩余间隔（tick）。 */
    private int soundCooldown;

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

        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            // 线程已满时不再接收新燃料棒，等有空闲线程再放开输入，避免物品滞留在看不见的缓存里
            IInventorySlot slot = BasicInventorySlot.at(
                  (stack, automationType) -> automationType == AutomationType.INTERNAL,
                  (stack, automationType) -> hasFreeThread() && isAcceptedFuel(stack),
                  MeknucSpentFuelPoolMultiblockData::isAcceptedFuel, saveAndComparator, -1000, -1000);
            this.inputSlots.add(slot);
            this.inventorySlots.add(slot);
        }

        for (int i = 0; i < MAX_COOLED_RODS; i++) {
            // 输出槽相反：任何人都能取走冷却完成的燃料棒，但不能再放回东西。
            IInventorySlot slot = BasicInventorySlot.at(
                  (stack, automationType) -> true,
                  (stack, automationType) -> false,
                  MeknucSpentFuelPoolMultiblockData::isCooledRod, saveAndComparator, -1000, -1000);
            this.outputSlots.add(slot);
            this.inventorySlots.add(slot);
        }
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
            this.waterCapacity = volume * FLUID_PER_VOLUME;
            this.steamCapacity = volume * FLUID_PER_VOLUME;
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
        return threads;
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
        return isRemote() ? clientRunningThreads : threads.size();
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
        return isRemote() || threads.size() < getMaxThreads();
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (!isFormed()) {
            return needsPacket;
        }
        handleValveIO(world);
        int cooled = getCooledCount();
        boolean paused = cooled >= MAX_COOLED_RODS
              || waterTank.getFluidAmount() <= 0
              || steamTank.getStored() >= steamTank.getCapacity();
        if (!paused) {
            startThreads(cooled);
        }
        if (threads.isEmpty()) {
            soundCooldown = 0;
            return needsPacket;
        }
        int waterAvailable = waterTank.getFluidAmount();
        long steamRoom = steamTank.getCapacity() - steamTank.getStored();
        int runnable = paused ? 0 : Math.min(threads.size(), (int) Math.min(waterAvailable / WATER_PER_THREAD_PER_TICK,
              steamRoom / STEAM_PER_THREAD_PER_TICK));
        if (runnable > 0) {
            waterTank.extract(runnable * WATER_PER_THREAD_PER_TICK, Action.EXECUTE, AutomationType.INTERNAL);
            steamTank.insert(MekanismChemicals.STEAM.asStack((long) runnable * STEAM_PER_THREAD_PER_TICK),
                  Action.EXECUTE, AutomationType.INTERNAL);
        }
        boolean changed = false;
        for (int i = 0; i < runnable; i++) {
            RodThread thread = threads.get(i);
            if (thread.progress < COOLING_TICKS) {
                thread.progress++;
                changed = true;
            }
        }
        for (int i = threads.size() - 1; i >= 0; i--) {
            RodThread thread = threads.get(i);
            if (thread.progress >= COOLING_TICKS && addCooledRod(thread.type)) {
                threads.remove(i);
                changed = true;
            }
        }
        if (changed && ++dirtyCounter % 20 == 0) {
            markDirty();
        }
        tickSound(world);
        return needsPacket;
    }

    /**
     * 乏燃料池运行时的盖革计数器音效：以随机间隔播放单次咔嗒声，
     * 模拟高辐射环境下的随机计数（间隔 2–9 tick，平均约 0.28 秒一次），
     * 与正在冷却的燃料棒数量无关。仅在服务端调用（由主方块的 tick 触发），
     * 由 {@link Level#playSound} 广播给附近玩家。
     */
    private void tickSound(Level world) {
        if (world.isClientSide) {
            return;
        }
        if (threads.isEmpty()) {
            soundCooldown = 0;
            return;
        }
        if (soundCooldown > 0) {
            soundCooldown--;
            return;
        }
        soundCooldown = MIN_CLICK_INTERVAL_TICKS + world.random.nextInt(RANDOM_CLICK_EXTRA_TICKS + 1);
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();
        BlockPos center = new BlockPos((min.getX() + max.getX()) / 2,
              (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
        world.playSound(null, center, MeknucSpentFuelPoolSounds.SPENT_FUEL_POOL_GEIGER.get(),
              SoundSource.BLOCKS, 0.35F, 1.0F);
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

    private void startThreads(int cooled) {
        if (cooled >= MAX_COOLED_RODS) {
            return;
        }
        int maxThreads = getMaxThreads();
        while (threads.size() < maxThreads) {
            IInventorySlot source = selectNextInputSlot();
            if (source == null) {
                return;
            }
            MeknucRodType type = MeknucItemFuelRodBase.getRodType(source.getStack());
            source.extractItem(1, Action.EXECUTE, AutomationType.INTERNAL);
            threads.add(new RodThread(type, 0));
            markDirty();
        }
    }

    /**
     * 选择下一个输入槽：优先取“当前正在冷却的线程数最少”的燃料棒种类。
     * 这样不同种类会轮流分配到线程，避免先放进来的种类占满线程数后，
     * 其他种类必须等它全部冷却完才能开始。
     */
    private IInventorySlot selectNextInputSlot() {
        Map<MeknucRodType, Integer> running = new EnumMap<>(MeknucRodType.class);
        for (RodThread thread : threads) {
            running.merge(thread.getType(), 1, Integer::sum);
        }
        IInventorySlot best = null;
        int bestRunning = Integer.MAX_VALUE;
        for (IInventorySlot slot : inputSlots) {
            if (slot.isEmpty() || !isAcceptedFuel(slot.getStack())) {
                continue;
            }
            int count = running.getOrDefault(MeknucItemFuelRodBase.getRodType(slot.getStack()), 0);
            if (count < bestRunning) {
                best = slot;
                bestRunning = count;
            }
        }
        return best;
    }

    private boolean addCooledRod(MeknucRodType type) {
        for (IInventorySlot slot : outputSlots) {
            if (slot.isEmpty()) {
                slot.setStack(MeknucItemFuelRodBase.depletedStack(type, 1));
                return true;
            }
        }
        return false;
    }

    public List<RodThread> copyThreads() {
        List<RodThread> copy = new ArrayList<>(threads.size());
        for (RodThread thread : threads) {
            copy.add(new RodThread(thread.type, thread.progress));
        }
        return copy;
    }

    public void restoreThreads(List<RodThread> restored) {
        threads.clear();
        int max = getMaxThreads();
        for (RodThread thread : restored) {
            if (max > 0 && threads.size() >= max) {
                break;
            }
            threads.add(new RodThread(thread.type, thread.progress));
        }
        markDirty();
    }

    /**
     * 单根燃料棒的冷却线程。
     */
    public static final class RodThread {

        private final MeknucRodType type;
        private int progress;

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
            return "RodThread[" + type + ", " + progress + "/" + COOLING_TICKS + "]";
        }
    }
}
