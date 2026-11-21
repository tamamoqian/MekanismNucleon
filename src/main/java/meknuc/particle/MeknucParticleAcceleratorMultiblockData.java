package meknuc.particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import meknuc.blocks.MeknucParticleAcceleratorBlockTypes.Mode;
import meknuc.chemicals.MeknucChemicals;
import meknuc.particle.MeknucParticleAcceleratorRecipes.ChemicalInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 大型环形粒子加速器的多方块数据。
 * <p>
 * 内部包含：
 * <ul>
 *   <li>16 GFE 的能量缓存（只能从“输入能量”端口输入，缓存满后自动停止输入）；</li>
 *   <li>按化学品分开的粒子束缓存（氢气 / 热质子等离子体 / 热中子等离子体，每种 128,000 mB）；</li>
 *   <li>靶材输入槽（只接受配方指定的物品）与靶材输出槽；</li>
 *   <li>工作模式（粒子加速 / 束流收集）、自动检测到的配方、加工进度与运行状态。</li>
 * </ul>
 * 不再需要手动选择配方：每 tick 自动在当前模式里寻找可以运行的配方并直接开跑。
 */
public class MeknucParticleAcceleratorMultiblockData extends MultiblockData {

    /** 内部能量缓存上限：16 GFE。 */
    public static final long ENERGY_CAPACITY = 16_000_000_000L;

    /** 输入粒子束（氢气）的缓存上限：128,000 mB。 */
    public static final long CHEMICAL_CAPACITY = 128_000L;

    /** 已加工完成的热质子 / 热中子等离子体缓存上限：每种 64,000 mB；满了就停机，直到从「输出粒子束」端口导出。 */
    public static final long PRODUCT_CAPACITY = 64_000L;

    /** 靶材槽位的内部上限（反物质靶丸配方一次需要 100 个钋粉）。 */
    public static final int SLOT_LIMIT = 1_000;

    /** 运行状态。 */
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_MISSING_ITEM = 2;
    public static final int STATUS_MISSING_CHEMICAL = 3;
    public static final int STATUS_OUTPUT_FULL = 4;
    public static final int STATUS_NO_ENERGY = 5;
    /** 当前模式里没有检测到任何可以运行的配方。 */
    public static final int STATUS_NO_RECIPE = 6;

    private static final int RECHECK_INTERVAL = 200;

    /** 能量端口只允许输入，内部逻辑可以抽取；未成形时拒绝外部输入。 */
    public final BasicEnergyContainer energyContainer;

    @ContainerSync
    public final IChemicalTank hydrogenTank;
    @ContainerSync
    public final IChemicalTank hotProtonTank;
    @ContainerSync
    public final IChemicalTank hotNeutronTank;

    /** GUI 右侧按“氢气、热质子等离子体、热中子等离子体”的顺序显示。 */
    private final List<IChemicalTank> displayTanks;
    /** 结构完整性抽查点（钢质机壳 / 靶件框架），只有这些方块消失时才重新校验结构。 */
    private final List<BlockPos> monitoredPositions = new ArrayList<>();
    private final List<Block> monitoredBlocks = new ArrayList<>();
    private final Map<DeferredChemical<Chemical>, IChemicalTank> tanksByChemical = new HashMap<>();

    private final IInventorySlot targetInputSlot;
    private final IInventorySlot targetOutputSlot;

    @ContainerSync(getter = "getStoredEnergyDisplay", setter = "setStoredEnergyDisplay")
    private double storedEnergyDisplay;

    @ContainerSync(getter = "getMode", setter = "setModeValue")
    private int mode = MeknucParticleAcceleratorRecipes.MODE_ACCELERATION;

    /** 自动检测到的配方下标；-1 表示当前没有候选配方。 */
    @ContainerSync(getter = "getActiveRecipeIndex", setter = "setActiveRecipeIndex")
    private int activeRecipeIndex = -1;

    @ContainerSync(getter = "getProgressTicks", setter = "setProgressTicks")
    private int progressTicks;

    @ContainerSync(getter = "getRecipeDuration", setter = "setRecipeDuration")
    private int recipeDuration;

    @ContainerSync(getter = "getActiveEnergyPerTick", setter = "setActiveEnergyPerTick")
    private double activeEnergyPerTick;

    /** 能量升级后的内部缓存上限（服务端计算，经容器同步给 GUI）。 */
    @ContainerSync(getter = "getEnergyCapacity", setter = "setEnergyCapacity")
    private long energyCapacity = ENERGY_CAPACITY;

    /** 成形结构的主方块（控制器）；速度 / 能量升级数据都挂在它身上，未成形时为 null。 */
    private TileEntityMekanism upgradeTile;

    @ContainerSync(getter = "getStatus", setter = "setStatus")
    private int status = STATUS_IDLE;

    @ContainerSync(getter = "getTargetInputStack", setter = "setTargetInputStack")
    private ItemStack targetInputStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getTargetOutputStack", setter = "setTargetOutputStack")
    private ItemStack targetOutputStack = ItemStack.EMPTY;

    public MeknucParticleAcceleratorMultiblockData(BlockEntity tile) {
        super(tile);
        energyContainer = new ScaledEnergyContainer(this::computeCapacity,
              type -> type == AutomationType.INTERNAL,
              type -> type == AutomationType.INTERNAL || isFormed(),
              this);
        energyContainers.add(energyContainer);

        hydrogenTank = createTank(MekanismChemicals.HYDROGEN, CHEMICAL_CAPACITY);
        hotProtonTank = createTank(MeknucChemicals.HOT_PROTON_PLASMA, PRODUCT_CAPACITY);
        hotNeutronTank = createTank(MeknucChemicals.HOT_NEUTRON_PLASMA, PRODUCT_CAPACITY);
        chemicalTanks.add(hydrogenTank);
        chemicalTanks.add(hotProtonTank);
        chemicalTanks.add(hotNeutronTank);
        // 右侧从顶部往下依次显示 氢气、热质子等离子体、热中子等离子体
        displayTanks = List.of(hydrogenTank, hotProtonTank, hotNeutronTank);

        targetInputSlot = new TargetSlot(SLOT_LIMIT, ConstantPredicates.alwaysTrueBi(),
              ConstantPredicates.alwaysTrueBi(), MeknucParticleAcceleratorRecipes::isTargetInput, this, 146, 112);
        targetOutputSlot = new TargetSlot(SLOT_LIMIT, ConstantPredicates.alwaysTrueBi(),
              ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), this, 168, 112);
        inventorySlots.add(targetInputSlot);
        inventorySlots.add(targetOutputSlot);
    }

    private IChemicalTank createTank(DeferredChemical<Chemical> chemical, long capacity) {
        IChemicalTank tank = VariableCapacityChemicalTank.create(this, () -> capacity,
              stack -> stack.is(chemical.get()), this);
        tanksByChemical.put(chemical, tank);
        return tank;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            tickRecipe();
            syncDisplay();
            if (world.getGameTime() % RECHECK_INTERVAL == 0 && !integrityIntact(world)) {
                // 只在抽查点真的被破坏时才整环复检，避免正常运行时重置多方块数据、关闭 GUI
                recheckStructure = true;
            }
        }
        return needsPacket;
    }

    private void syncDisplay() {
        long capacity = computeCapacity();
        if (energyCapacity != capacity) {
            energyCapacity = capacity;
        }
        if (energyContainer.getEnergy() > capacity) {
            // 拆掉能量升级后上限变小，超出的部分直接截断
            energyContainer.setEnergy(capacity);
        }
        double energy = energyContainer.getEnergy();
        if (storedEnergyDisplay != energy) {
            storedEnergyDisplay = energy;
        }
        ItemStack input = targetInputSlot.getStack();
        if (!ItemStack.matches(targetInputStack, input)) {
            targetInputStack = input.copy();
        }
        ItemStack output = targetOutputSlot.getStack();
        if (!ItemStack.matches(targetOutputStack, output)) {
            targetOutputStack = output.copy();
        }
    }

    /**
     * 每 tick 的配方逻辑：没有进度时自动检测并启动当前模式里第一条可运行的配方。
     */
    private void tickRecipe() {
        List<MeknucParticleAcceleratorRecipes.Recipe> recipes = MeknucParticleAcceleratorRecipes.recipes();

        // 已投入材料、正在推进：继续这一条配方
        if (progressTicks > 0) {
            MeknucParticleAcceleratorRecipes.Recipe recipe = recipeAt(recipes, activeRecipeIndex);
            if (recipe == null) {
                resetProgress();
                activeRecipeIndex = -1;
                status = STATUS_IDLE;
                return;
            }
            recipeDuration = scaledDuration(recipe);
            activeEnergyPerTick = scaledEnergyPerTick(recipe);
            if (!consumeEnergy(recipe)) {
                status = STATUS_NO_ENERGY;
                return;
            }
            progressTicks++;
            if (progressTicks >= recipeDuration) {
                finishRecipe(recipe);
            } else {
                status = STATUS_RUNNING;
            }
            return;
        }

        int index = findStartableRecipe(recipes);
        if (index < 0) {
            reportBlockedRecipe(recipes);
            return;
        }

        MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(index);
        activeRecipeIndex = index;
        recipeDuration = scaledDuration(recipe);
        activeEnergyPerTick = scaledEnergyPerTick(recipe);

        // 开始加工：一次性扣掉靶材与全部粒子束
        if (recipe.hasItemInput()) {
            targetInputSlot.extractItem(recipe.inputCount(), Action.EXECUTE, AutomationType.INTERNAL);
        }
        for (ChemicalInput chemicalInput : recipe.chemicalInputs()) {
            IChemicalTank tank = tanksByChemical.get(chemicalInput.chemical());
            if (tank != null) {
                tank.extract(chemicalInput.amount(), Action.EXECUTE, AutomationType.INTERNAL);
            }
        }
        if (!consumeEnergy(recipe)) {
            status = STATUS_NO_ENERGY;
            return;
        }
        progressTicks = 1;
        status = STATUS_RUNNING;
        if (progressTicks >= recipeDuration) {
            finishRecipe(recipe);
        }
        markDirty();
    }

    private MeknucParticleAcceleratorRecipes.Recipe recipeAt(
          List<MeknucParticleAcceleratorRecipes.Recipe> recipes, int index) {
        if (index < 0 || index >= recipes.size()) {
            return null;
        }
        return recipes.get(index);
    }

    /**
     * 选一条当前能跑的配方：
     * <ol>
     *   <li>优先「有靶材」的配方 —— 玩家把物品放进靶材槽就是明确指令，
     *       束流收集模式下因此不会一直卡在只吃氢气的第一条配方上；</li>
     *   <li>其次沿用上一条仍在运行的配方；</li>
     *   <li>最后才是当前模式里任意一条可运行的配方。</li>
     * </ol>
     */
    private int findStartableRecipe(List<MeknucParticleAcceleratorRecipes.Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
            if (recipe.hasItemInput() && isStartable(recipes, i)) {
                return i;
            }
        }
        if (isStartable(recipes, activeRecipeIndex)) {
            return activeRecipeIndex;
        }
        for (int i = 0; i < recipes.size(); i++) {
            if (isStartable(recipes, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isStartable(List<MeknucParticleAcceleratorRecipes.Recipe> recipes, int index) {
        MeknucParticleAcceleratorRecipes.Recipe recipe = recipeAt(recipes, index);
        return recipe != null && recipe.mode() == mode && hasTarget(recipe) && hasChemicals(recipe)
              && canStoreOutput(recipe) && energyContainer.getEnergy() >= scaledEnergyPerTick(recipe);
    }

    /** 没有可运行的配方时，挑一条最接近的配方，让 GUI 显示到底缺什么。 */
    private void reportBlockedRecipe(List<MeknucParticleAcceleratorRecipes.Recipe> recipes) {
        progressTicks = 0;
        recipeDuration = 0;
        activeEnergyPerTick = 0.0D;
        ItemStack input = targetInputSlot.getStack();

        int candidate = -1;
        // 优先展示与靶材槽里物品对应的配方（即使数量还不够），而不是永远显示第一条
        if (!input.isEmpty()) {
            for (int i = 0; i < recipes.size(); i++) {
                MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
                if (recipe.mode() == mode && recipe.hasItemInput() && input.is(recipe.input().getItem())) {
                    candidate = i;
                    break;
                }
            }
        }
        if (candidate < 0) {
            for (int i = 0; i < recipes.size(); i++) {
                MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
                if (recipe.mode() == mode && !recipe.hasItemInput() && hasTarget(recipe)) {
                    candidate = i;
                    break;
                }
            }
        }
        if (candidate < 0) {
            activeRecipeIndex = -1;
            status = STATUS_NO_RECIPE;
            return;
        }

        activeRecipeIndex = candidate;
        MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(candidate);
        recipeDuration = scaledDuration(recipe);
        activeEnergyPerTick = scaledEnergyPerTick(recipe);
        if (!hasTarget(recipe)) {
            status = STATUS_MISSING_ITEM;
        } else if (!hasChemicals(recipe)) {
            status = STATUS_MISSING_CHEMICAL;
        } else if (!canStoreOutput(recipe)) {
            status = STATUS_OUTPUT_FULL;
        } else if (energyContainer.getEnergy() < recipe.energyPerTick()) {
            status = STATUS_NO_ENERGY;
        } else {
            status = STATUS_IDLE;
        }
    }

    private boolean hasTarget(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (!recipe.hasItemInput()) {
            return true;
        }
        ItemStack stack = targetInputSlot.getStack();
        return !stack.isEmpty() && stack.is(recipe.input().getItem()) && stack.getCount() >= recipe.inputCount();
    }

    private boolean hasChemicals(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        for (ChemicalInput chemicalInput : recipe.chemicalInputs()) {
            IChemicalTank tank = tanksByChemical.get(chemicalInput.chemical());
            if (tank == null || tank.getStored() < chemicalInput.amount()) {
                return false;
            }
        }
        return true;
    }

    private boolean consumeEnergy(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        long energyPerTick = scaledEnergyPerTick(recipe);
        if (energyContainer.getEnergy() < energyPerTick) {
            return false;
        }
        return energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL) >= energyPerTick;
    }

    private boolean canStoreOutput(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (recipe.hasChemicalOutput()) {
            IChemicalTank tank = tanksByChemical.get(recipe.chemicalOutput());
            return tank != null && tank.getStored() + recipe.chemicalOutputAmount() <= tank.getCapacity();
        }
        ItemStack result = recipe.output();
        ItemStack current = targetOutputSlot.getStack();
        if (current.isEmpty()) {
            return result.getCount() <= targetOutputSlot.getLimit(result);
        }
        return ItemStack.isSameItemSameComponents(current, result)
              && current.getCount() + result.getCount() <= targetOutputSlot.getLimit(result);
    }

    private void finishRecipe(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (recipe.hasChemicalOutput()) {
            IChemicalTank tank = tanksByChemical.get(recipe.chemicalOutput());
            if (tank != null) {
                tank.insert(recipe.chemicalOutputStack(), Action.EXECUTE, AutomationType.INTERNAL);
            }
        } else {
            targetOutputSlot.insertItem(recipe.output().copy(), Action.EXECUTE, AutomationType.INTERNAL);
        }
        progressTicks = 0;
        status = STATUS_RUNNING;
        markDirty();
    }

    /**
     * 切换工作模式：粒子加速 / 束流收集。切换会放弃当前这一份已投入的材料。
     */
    public boolean changeMode(int newMode) {
        if (newMode != MeknucParticleAcceleratorRecipes.MODE_ACCELERATION
              && newMode != MeknucParticleAcceleratorRecipes.MODE_COLLECTION) {
            return false;
        }
        if (mode == newMode) {
            return true;
        }
        resetProgress();
        activeRecipeIndex = -1;
        status = STATUS_IDLE;
        markDirty();
        // 等服务端 tick 下一 tick 再按新模式的配方重新自动检测
        mode = newMode;
        return true;
    }

    private void resetProgress() {
        progressTicks = 0;
        recipeDuration = 0;
        activeEnergyPerTick = 0.0D;
    }

    /**
     * 抽查几个特征方块是否还在；正常运行时返回 true，不会触发重新校验。
     */
    private boolean integrityIntact(Level world) {
        for (int i = 0; i < monitoredPositions.size(); i++) {
            if (world.getBlockState(monitoredPositions.get(i)).getBlock() != monitoredBlocks.get(i)) {
                return false;
            }
        }
        return true;
    }

    public void setMonitoredBlocks(List<BlockPos> positions, List<Block> blocks) {
        monitoredPositions.clear();
        monitoredPositions.addAll(positions);
        monitoredBlocks.clear();
        monitoredBlocks.addAll(blocks);
    }

    // ---- 升级：速度 / 能量 ----

    /**
     * 由成形后的主方块（控制器）登记自己：多方块里的升级都装在控制器上，
     * 数据类通过它读取已安装的升级数量。
     */
    public void setUpgradeTile(TileEntityMekanism tile) {
        this.upgradeTile = tile;
    }

    /** 能量升级后的内部缓存上限。 */
    public long getEnergyCapacity() {
        return energyCapacity;
    }

    public void setEnergyCapacity(long energyCapacity) {
        this.energyCapacity = energyCapacity;
    }

    private long computeCapacity() {
        if (upgradeTile != null && upgradeTile.supportsUpgrade(Upgrade.ENERGY)) {
            return MekanismUtils.getMaxEnergy(upgradeTile, ENERGY_CAPACITY);
        }
        return ENERGY_CAPACITY;
    }

    /** 速度升级：缩短配方所需 tick（与其他 Mekanism 机器一致）。 */
    private int scaledDuration(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (upgradeTile == null) {
            return recipe.durationTicks();
        }
        return MekanismUtils.getTicks(upgradeTile, recipe.durationTicks());
    }

    /** 速度 / 能量升级：每 tick 能耗；速度提升会按平方关系抬高维持功率。 */
    private long scaledEnergyPerTick(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (upgradeTile == null) {
            return recipe.energyPerTick();
        }
        return MekanismUtils.getEnergyPerTick(upgradeTile, recipe.energyPerTick());
    }

    /** 当前自动检测到的配方（可能是正在运行的，也可能是缺材料的那一条）。 */
    public MeknucParticleAcceleratorRecipes.Recipe getActiveRecipe() {
        return recipeAt(MeknucParticleAcceleratorRecipes.recipes(), activeRecipeIndex);
    }

    public boolean isRunning() {
        return progressTicks > 0;
    }

    public List<IInventorySlot> getInventorySlots(Mode portMode) {
        return switch (portMode) {
            case TARGET_INPUT -> List.of(targetInputSlot);
            case TARGET_OUTPUT -> List.of(targetOutputSlot);
            default -> Collections.emptyList();
        };
    }

    public List<IChemicalTank> getChemicalTanks(Mode portMode) {
        return switch (portMode) {
            // 输入端口：氢气（束流收集原料）与两种等离子体（粒子加速原料）都可以打进来
            case BEAM_INPUT -> chemicalTanks;
            // 输出端口：只导出已经加工好的热质子 / 热中子等离子体，不会把氢气抽走
            case BEAM_OUTPUT -> List.of(hotProtonTank, hotNeutronTank);
            default -> Collections.emptyList();
        };
    }

    public List<IEnergyContainer> getEnergyContainers(Mode portMode) {
        return portMode == Mode.ENERGY ? List.of(energyContainer) : Collections.emptyList();
    }

    public List<IChemicalTank> getDisplayTanks() {
        return displayTanks;
    }

    /** 控制器和端口共用的一对靶材槽（输入 + 输出）。 */
    public List<IInventorySlot> getTargetSlots() {
        return List.of(targetInputSlot, targetOutputSlot);
    }

    public IInventorySlot getTargetInputSlot() {
        return targetInputSlot;
    }

    public IInventorySlot getTargetOutputSlot() {
        return targetOutputSlot;
    }

    public double getStoredEnergyDisplay() {
        return storedEnergyDisplay;
    }

    public void setStoredEnergyDisplay(double storedEnergyDisplay) {
        this.storedEnergyDisplay = storedEnergyDisplay;
    }

    public int getMode() {
        return mode;
    }

    public void setModeValue(int mode) {
        this.mode = mode;
    }

    public int getActiveRecipeIndex() {
        return activeRecipeIndex;
    }

    public void setActiveRecipeIndex(int activeRecipeIndex) {
        this.activeRecipeIndex = activeRecipeIndex;
    }

    public int getProgressTicks() {
        return progressTicks;
    }

    public void setProgressTicks(int progressTicks) {
        this.progressTicks = progressTicks;
    }

    public int getRecipeDuration() {
        return recipeDuration;
    }

    public void setRecipeDuration(int recipeDuration) {
        this.recipeDuration = recipeDuration;
    }

    public double getActiveEnergyPerTick() {
        return activeEnergyPerTick;
    }

    public void setActiveEnergyPerTick(double activeEnergyPerTick) {
        this.activeEnergyPerTick = activeEnergyPerTick;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ItemStack getTargetInputStack() {
        return targetInputStack;
    }

    public void setTargetInputStack(ItemStack targetInputStack) {
        this.targetInputStack = targetInputStack;
    }

    public ItemStack getTargetOutputStack() {
        return targetOutputStack;
    }

    public void setTargetOutputStack(ItemStack targetOutputStack) {
        this.targetOutputStack = targetOutputStack;
    }

    public void restoreState(int activeRecipeIndex, int progressTicks, int recipeDuration, double activeEnergyPerTick,
          int status, int mode) {
        this.activeRecipeIndex = activeRecipeIndex;
        this.progressTicks = progressTicks;
        this.recipeDuration = recipeDuration;
        this.activeEnergyPerTick = activeEnergyPerTick;
        this.status = status;
        this.mode = mode;
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        storedEnergyDisplay = energyContainer.getEnergy();
    }

    /**
     * 容量随能量升级变化的能量容器：16 GFE 起，8 级能量升级后到 160 GFE。
     */
    private static class ScaledEnergyContainer extends BasicEnergyContainer {

        private final LongSupplier capacity;

        private ScaledEnergyContainer(LongSupplier capacity, Predicate<AutomationType> canExtract,
              Predicate<AutomationType> canInsert, IContentsListener listener) {
            super(capacity.getAsLong(), canExtract, canInsert, listener);
            this.capacity = capacity;
        }

        @Override
        public long getMaxEnergy() {
            return capacity.getAsLong();
        }
    }

    /** 靶材槽：可设置内部上限，不受原版 64 堆叠限制。 */
    private static class TargetSlot extends BasicInventorySlot {

        private TargetSlot(int limit, BiPredicate<ItemStack, AutomationType> canExtract,
              BiPredicate<ItemStack, AutomationType> canInsert, Predicate<ItemStack> validator, IContentsListener listener,
              int guiX, int guiY) {
            super(limit, canExtract, canInsert, validator, listener, guiX, guiY);
            obeyStackLimit = false;
        }
    }
}
