package meknuc.wastestorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.WorldUtils;
import meknuc.blocks.MeknucWasteStorageBlockTypes;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucItemRadioactive;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolSounds;
import meknuc.wastestorage.tile.TileEntityWasteStoragePort;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 放射性废料存储罐的多方块数据。
 * <p>
 * 内部划分为两块独立空间：
 * <ul>
 *     <li><b>废料物品存储空间</b>：只能存放射性物品（各种放射性粉尘 + 任何燃料棒），容量 = 多方块体积 × 16；</li>
 *     <li><b>衰变完成产物存储空间</b>：只用来缓存衰变产物，容量同样 = 多方块体积 × 16。</li>
 * </ul>
 * 两条空间都按「物品个数」计算容量，槽位只是载体（每格最多 64 个，总上限由体积决定）。
 * <p>
 * 衰变逻辑：
 * <ul>
 *     <li>按「并行线程」运行：线程数 = 多方块体积，每个线程独立处理 1 个废料物品；</li>
 *     <li>五条配方共用这个线程池，线程启动时从废料空间扣掉 1 个对应输入，到时间后往产物空间放入 1 个产物；</li>
 *     <li>只有当废料空间里的放射性物品数量达到并行线程数时才会开新线程，避免少量物品把线程铺满；</li>
 *     <li>产物空间已满时全部线程暂停，直到输出端口把产物抽走；</li>
 *     <li>燃料棒不参与衰变，只在废料空间里做存储，可以从「输出衰变产物」端口直接抽出；</li>
 *     <li>运行时播放与乏燃料冷却池相同的盖革计数器咔嗒声。</li>
 * </ul>
 * 端口：
 * <ul>
 *     <li>{@code RADIOACTIVE_WASTE_INPUT}：只暴露废料空间的「插入」视图，不能抽出；</li>
 *     <li>{@code DECAY_PRODUCT_OUTPUT}：暴露产物空间的「抽出」视图，外加废料空间里燃料棒的「抽出」视图，
 *         不能插入。</li>
 * </ul>
 */
public class MeknucWasteStorageMultiblockData extends MultiblockData {

    /** 每个物品格最多堆放的数量。 */
    public static final int SLOT_LIMIT = 64;
    /** 废料空间的物品格数量（64 × 64 = 4096 > 3×3×18 的体积上限 2592，够用且种类余量充足）。 */
    public static final int WASTE_SLOT_COUNT = 64;
    /** 衰变完成产物空间的物品格数量。 */
    public static final int PRODUCT_SLOT_COUNT = 64;
    /** 每 1 格多方块体积对应的物品储量。 */
    public static final int ITEMS_PER_VOLUME = 16;
    /** 进度写回缓存的间隔（tick）。 */
    private static final int SAVE_INTERVAL = 20;
    /** 运行音效最小间隔（tick），与乏燃料冷却池一致。 */
    public static final int MIN_CLICK_INTERVAL_TICKS = 2;
    /** 在最小间隔上再随机 0..N tick，模拟不规则的盖革计数。 */
    public static final int RANDOM_CLICK_EXTRA_TICKS = 7;

    private final List<IInventorySlot> wasteSlots = new ArrayList<>(WASTE_SLOT_COUNT);
    private final List<IInventorySlot> productSlots = new ArrayList<>(PRODUCT_SLOT_COUNT);
    /** 输入端口看到的视图：只能插入废料空间。 */
    private final List<IInventorySlot> inputPortSlots = new ArrayList<>(WASTE_SLOT_COUNT);
    /** 输出端口看到的视图：产物空间（可抽出）+ 废料空间里的燃料棒（可抽出）。 */
    private final List<IInventorySlot> outputPortSlots =
          new ArrayList<>(PRODUCT_SLOT_COUNT + WASTE_SLOT_COUNT);

    /** 正在运行的并行衰变线程（每个线程处理 1 个废料物品）。 */
    private final List<DecayThread> threads = new ArrayList<>();

    /**
     * GUI 显示用的「代表进度」：每条配方取进度最高的线程（tick），0 表示该配方没有线程。
     * 数组通过 {@code @ContainerSync} 逐项同步给客户端（getter 必须返回数组本身）。
     */
    @ContainerSync(getter = "getSyncedProgress")
    private final int[] progress = new int[MeknucWasteStorageRecipes.RECIPE_COUNT];

    /** 每条配方正在运行的线程数，同样通过容器同步给 GUI。 */
    @ContainerSync(getter = "getSyncedThreadCounts")
    private final int[] threadCounts = new int[MeknucWasteStorageRecipes.RECIPE_COUNT];

    /** 并行线程上限 = 多方块体积（服务端计算，客户端通过容器同步拿到）。 */
    @ContainerSync(getter = "getMaxThreads")
    public int clientMaxThreads;

    /** 盖革计数器运行音效的剩余间隔（tick）。 */
    private int soundCooldown;

    /** 产物空间是否已满（GUI 显示「暂停」用）。 */
    @ContainerSync(getter = "getSyncedProductFull", setter = "setSyncedProductFull")
    private boolean productFull;

    private int dirtyCounter;

    public MeknucWasteStorageMultiblockData(net.minecraft.world.level.block.entity.BlockEntity tile) {
        super(tile);
        IContentsListener saveAndComparator = createSaveAndComparator();
        for (int i = 0; i < WASTE_SLOT_COUNT; i++) {
            StorageSlot slot = new StorageSlot(true, saveAndComparator);
            wasteSlots.add(slot);
            inventorySlots.add(slot);
            inputPortSlots.add(new PortSlot(slot, PortSlot.Mode.WASTE_INPUT));
        }
        for (int i = 0; i < PRODUCT_SLOT_COUNT; i++) {
            StorageSlot slot = new StorageSlot(false, saveAndComparator);
            productSlots.add(slot);
            inventorySlots.add(slot);
            outputPortSlots.add(new PortSlot(slot, PortSlot.Mode.PRODUCT_OUTPUT));
        }
        // 输出端口还能抽出废料空间里的燃料棒（燃料棒只存储、不衰变）
        for (IInventorySlot slot : wasteSlots) {
            outputPortSlots.add(new PortSlot(slot, PortSlot.Mode.FUEL_ROD_OUTPUT));
        }
    }

    // ------------------------------------------------------------------ 判定

    /** 能否存进废料空间：任何放射性物品（放射性粉尘、燃料棒，含枯竭/热燃料棒）。 */
    public static boolean isRadioactiveItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof MeknucItemRadioactive;
    }

    public static boolean isFuelRod(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof MeknucItemFuelRodBase;
    }

    // ------------------------------------------------------------------ 容量

    /** 废料空间物品上限 = 多方块体积 × 16。 */
    public int getWasteCapacity() {
        return getVolume() * ITEMS_PER_VOLUME;
    }

    /** 衰变完成产物缓存上限 = 多方块体积 × 16。 */
    public int getProductCapacity() {
        return getVolume() * ITEMS_PER_VOLUME;
    }

    public int getStoredWasteCount() {
        return count(wasteSlots);
    }

    public int getStoredProductCount() {
        return count(productSlots);
    }

    private static int count(List<IInventorySlot> slots) {
        int total = 0;
        for (IInventorySlot slot : slots) {
            total += slot.getCount();
        }
        return total;
    }

    public List<IInventorySlot> getWasteSlots() {
        return wasteSlots;
    }

    public List<IInventorySlot> getProductSlots() {
        return productSlots;
    }

    /**
     * 端口看到的槽位视图：输入端口只能插入，输出端口只能抽出。
     */
    public List<IInventorySlot> getPortSlots(MeknucWasteStorageBlockTypes.Mode mode) {
        return mode == MeknucWasteStorageBlockTypes.Mode.DECAY_PRODUCT_OUTPUT ? outputPortSlots : inputPortSlots;
    }

    /**
     * 手持 / 管道插入废料空间，返回剩余物品。
     */
    public ItemStack insertWaste(ItemStack stack, Action action, AutomationType automationType) {
        ItemStack remainder = stack;
        for (IInventorySlot slot : wasteSlots) {
            if (remainder.isEmpty()) {
                break;
            }
            remainder = slot.insertItem(remainder, action, automationType);
        }
        return remainder;
    }

    /**
     * 左侧 GUI 用：废料空间里除燃料棒以外的每种物品的存量，按首次出现顺序排列。
     * 燃料棒由 {@link #getStoredFuelRodCount()} 单独统计（所有燃料棒类型并入一行）。
     */
    public Map<Item, Integer> getWasteCountsByItem() {
        Map<Item, Integer> counts = new LinkedHashMap<>();
        for (IInventorySlot slot : wasteSlots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || isFuelRod(stack)) {
                continue;
            }
            counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }
        return counts;
    }

    public int getStoredFuelRodCount() {
        int total = 0;
        for (IInventorySlot slot : wasteSlots) {
            ItemStack stack = slot.getStack();
            if (isFuelRod(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    // ------------------------------------------------------------------ 进度

    /**
     * 容器同步用的进度数组 getter：必须返回数组本身（客户端会把同步值直接写进这个数组）。
     */
    public int[] getSyncedProgress() {
        return progress;
    }

    /**
     * 容器同步用的线程数数组 getter：同样必须返回数组本身。
     */
    public int[] getSyncedThreadCounts() {
        return threadCounts;
    }

    /**
     * 并行线程上限 = 多方块体积（客户端读同步值）。
     */
    public int getMaxThreads() {
        return isRemote() ? clientMaxThreads : getVolume();
    }

    /**
     * 当前正在运行的线程总数。
     */
    public int getRunningThreadCount() {
        int total = 0;
        for (int count : threadCounts) {
            total += count;
        }
        return total;
    }

    /**
     * 某条配方当前正在运行的线程数。
     */
    public int getThreadCount(int recipe) {
        return recipe >= 0 && recipe < threadCounts.length ? threadCounts[recipe] : 0;
    }

    public boolean getSyncedProductFull() {
        return productFull;
    }

    public void setSyncedProductFull(boolean value) {
        this.productFull = value;
    }

    public int getDecayProgress(int recipe) {
        return recipe >= 0 && recipe < progress.length ? progress[recipe] : 0;
    }

    public int getDecayPercent(int recipe) {
        List<MeknucWasteStorageRecipes.Recipe> recipes = MeknucWasteStorageRecipes.recipes();
        if (recipe < 0 || recipe >= recipes.size()) {
            return 0;
        }
        int duration = recipes.get(recipe).durationTicks();
        if (duration <= 0) {
            return 0;
        }
        return Math.min(100, getDecayProgress(recipe) * 100 / duration);
    }

    /** 产物空间是否已满（满了就暂停所有衰变进度）。 */
    public boolean isDecayPaused() {
        return productFull;
    }

    /**
     * 打包所有线程（配方下标与进度成对存放），供多方块缓存保存。
     */
    public int[] copyThreads() {
        int[] packed = new int[threads.size() * 2];
        for (int i = 0; i < threads.size(); i++) {
            DecayThread thread = threads.get(i);
            packed[i * 2] = thread.recipe;
            packed[i * 2 + 1] = thread.progressTicks;
        }
        return packed;
    }

    /**
     * 结构重新成型 / 读档时恢复并行线程；超出当前线程上限的部分按进度从高到低截断。
     */
    public void restoreThreads(int[] packed) {
        threads.clear();
        if (packed != null && packed.length >= 2) {
            List<DecayThread> restored = new ArrayList<>(packed.length / 2);
            for (int i = 0; i + 1 < packed.length; i += 2) {
                int recipe = packed[i];
                if (recipe >= 0 && recipe < MeknucWasteStorageRecipes.RECIPE_COUNT) {
                    restored.add(new DecayThread(recipe, Math.max(0, packed[i + 1])));
                }
            }
            restored.sort(Comparator.comparingInt((DecayThread thread) -> thread.progressTicks).reversed());
            int maxThreads = getMaxThreads();
            int limit = maxThreads > 0 ? Math.min(maxThreads, restored.size()) : restored.size();
            threads.addAll(restored.subList(0, limit));
        }
        syncDisplay();
        markDirty();
    }

    // ------------------------------------------------------------------ tick

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            handleValveIO(world);
            tickDecay(world);
        }
        return needsPacket;
    }

    /**
     * 衰变主循环：并行线程各自计时；产物缓存满了就整体暂停。
     */
    private void tickDecay(Level world) {
        List<MeknucWasteStorageRecipes.Recipe> recipes = MeknucWasteStorageRecipes.recipes();
        boolean full = getStoredProductCount() >= getProductCapacity();
        if (this.productFull != full) {
            this.productFull = full;
        }
        boolean changed = false;
        if (!full) {
            trimThreads();
            changed |= advanceThreads(recipes);
            changed |= finishThreads(recipes);
            changed |= startThreads(recipes);
        }
        syncDisplay();
        if (changed && ++dirtyCounter >= SAVE_INTERVAL) {
            dirtyCounter = 0;
            markDirty();
        }
        tickSound(world);
    }

    /**
     * 推进所有并行线程；产物缓存满了就不推进（暂停），等输出端口腾出空间。
     */
    private boolean advanceThreads(List<MeknucWasteStorageRecipes.Recipe> recipes) {
        boolean changed = false;
        for (DecayThread thread : threads) {
            MeknucWasteStorageRecipes.Recipe recipe = recipes.get(thread.recipe);
            if (thread.progressTicks < recipe.durationTicks()) {
                thread.progressTicks++;
                changed = true;
            }
        }
        return changed;
    }

    /**
     * 收掉到时间的线程：成功放入 1 个产物后才释放线程。
     */
    private boolean finishThreads(List<MeknucWasteStorageRecipes.Recipe> recipes) {
        boolean changed = false;
        for (int i = threads.size() - 1; i >= 0; i--) {
            DecayThread thread = threads.get(i);
            MeknucWasteStorageRecipes.Recipe recipe = recipes.get(thread.recipe);
            if (thread.progressTicks >= recipe.durationTicks() && insertProduct(recipe, 1)) {
                threads.remove(i);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * 开启新的并行线程。
     * <p>
     * 并行线程数 = 多方块体积；只有废料空间里的放射性物品数量**达到或超过**并行线程数时才会开新的一批，
     * 否则宁可空转等待，避免只存了几十个物品也把线程铺满。
     */
    private boolean startThreads(List<MeknucWasteStorageRecipes.Recipe> recipes) {
        int maxThreads = getMaxThreads();
        if (maxThreads <= 0 || threads.size() >= maxThreads) {
            return false;
        }
        if (getStoredWasteCount() < maxThreads) {
            return false;
        }
        boolean started = false;
        for (int i = 0; i < recipes.size() && threads.size() < maxThreads; i++) {
            MeknucWasteStorageRecipes.Recipe recipe = recipes.get(i);
            while (threads.size() < maxThreads && extractInput(recipe, 1) > 0) {
                // 启动即算第 1 tick，和旧版单线程的计时方式一致，GUI 也会立刻显示「运行中」
                threads.add(new DecayThread(i, 1));
                started = true;
            }
        }
        return started;
    }

    /**
     * 把服务端的线程情况写进 GUI 同步数组：每条配方取进度最高的线程作为显示进度。
     */
    private void syncDisplay() {
        Arrays.fill(progress, 0);
        Arrays.fill(threadCounts, 0);
        for (DecayThread thread : threads) {
            threadCounts[thread.recipe]++;
            progress[thread.recipe] = Math.max(progress[thread.recipe], thread.progressTicks);
        }
    }

    /**
     * 多方块缩小时线程上限会变小，把多出来的线程丢掉（保留先启动的）。
     */
    private void trimThreads() {
        int maxThreads = getMaxThreads();
        if (maxThreads <= 0) {
            return;
        }
        while (threads.size() > maxThreads) {
            threads.remove(threads.size() - 1);
        }
    }

    /**
     * 运行音效：复用乏燃料冷却池的盖革计数器咔嗒声，以 2–9 tick 的随机间隔播放，
     * 听感与乏燃料池一致；没有线程在跑时保持安静。
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
     * 从废料空间扣掉指定数量输入，返回实际扣掉的数量。
     */
    private int extractInput(MeknucWasteStorageRecipes.Recipe recipe, int amount) {
        int remaining = amount;
        for (IInventorySlot slot : wasteSlots) {
            if (remaining <= 0) {
                break;
            }
            ItemStack stack = slot.getStack();
            if (!recipe.matchesInput(stack)) {
                continue;
            }
            ItemStack extracted = slot.extractItem(remaining, Action.EXECUTE, AutomationType.INTERNAL);
            remaining -= extracted.getCount();
        }
        return amount - remaining;
    }

    /**
     * 往产物空间放入指定数量产物，只有全部放进去才返回 true。
     */
    private boolean insertProduct(MeknucWasteStorageRecipes.Recipe recipe, int amount) {
        ItemStack stack = recipe.outputStack(amount);
        for (IInventorySlot slot : productSlots) {
            if (stack.isEmpty()) {
                break;
            }
            if (!slot.isEmpty() && !ItemStack.isSameItemSameComponents(slot.getStack(), stack)) {
                continue;
            }
            stack = slot.insertItem(stack, Action.EXECUTE, AutomationType.INTERNAL);
        }
        return stack.isEmpty();
    }

    /**
     * 端口自动输出：只有「输出衰变产物」端口会把产物空间里的东西推到相邻物品容器。
     * 燃料棒不会被自动推出，只能由管道/漏斗从输出端口抽。
     */
    private void handleValveIO(Level world) {
        for (IValveHandler.ValveData valve : valves) {
            if (!(WorldUtils.getTileEntity(world, valve.location) instanceof TileEntityWasteStoragePort port)) {
                continue;
            }
            if (port.getPortMode() != MeknucWasteStorageBlockTypes.Mode.DECAY_PRODUCT_OUTPUT) {
                continue;
            }
            pushProducts(world, valve.location.relative(valve.side), valve.side.getOpposite());
        }
    }

    private void pushProducts(Level world, BlockPos target, Direction side) {
        if (getStoredProductCount() <= 0) {
            return;
        }
        IItemHandler handler = world.getCapability(Capabilities.ITEM.block(), target, side);
        if (handler == null) {
            return;
        }
        for (IInventorySlot slot : productSlots) {
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

    /**
     * 一个并行衰变线程：绑定一条配方、独立计时；启动时已经从废料空间扣掉了 1 个输入。
     */
    private static final class DecayThread {

        private final int recipe;
        private int progressTicks;

        private DecayThread(int recipe, int progressTicks) {
            this.recipe = recipe;
            this.progressTicks = progressTicks;
        }
    }

    // ------------------------------------------------------------------ 槽位

    /**
     * 容量按「物品个数」计算的存储槽：每格最多 64 个，并且整块空间的总数不能超过体积上限。
     */
    private final class StorageSlot extends BasicInventorySlot {

        private final boolean waste;

        private StorageSlot(boolean waste, IContentsListener listener) {
            super(SLOT_LIMIT,
                  ConstantPredicates.alwaysTrueBi(),
                  waste ? (stack, automationType) -> isRadioactiveItem(stack)
                        : (stack, automationType) -> automationType == AutomationType.INTERNAL,
                  waste ? MeknucWasteStorageMultiblockData::isRadioactiveItem : ConstantPredicates.alwaysTrue(),
                  listener, -1000, -1000);
            this.waste = waste;
            // 容量按「物品个数」计算，所以每格固定按 64 个算：
            // 燃料棒原版最多堆 16，但存储罐里同样一格最多放 64 根，容量上限才和体积 ×16 对得上。
            this.obeyStackLimit = false;
        }

        @Override
        public int getLimit(ItemStack stack) {
            int base = super.getLimit(stack);
            int capacity = waste ? getWasteCapacity() : getProductCapacity();
            int stored = waste ? getStoredWasteCount() : getStoredProductCount();
            int remaining = Math.max(0, capacity - stored);
            return Math.min(base, getCount() + remaining);
        }
    }

    /**
     * 端口的槽位视图：同一份存储按端口模式暴露不同的「可插入 / 可抽出」规则。
     * <p>
     * 视图只做权限过滤，真正的内容仍然写在 {@link StorageSlot} 上，
     * 所以管道、漏斗、容器同步看到的都是同一份数据。
     */
    private static final class PortSlot implements IInventorySlot {

        private enum Mode {
            /** 输入端口：只能插入废料空间。 */
            WASTE_INPUT,
            /** 输出端口：只能抽出衰变完成产物。 */
            PRODUCT_OUTPUT,
            /** 输出端口：只能抽出废料空间里的燃料棒。 */
            FUEL_ROD_OUTPUT
        }

        private final IInventorySlot backing;
        private final Mode mode;

        private PortSlot(IInventorySlot backing, Mode mode) {
            this.backing = backing;
            this.mode = mode;
        }

        @Override
        public ItemStack getStack() {
            return backing.getStack();
        }

        @Override
        public void setStack(ItemStack stack) {
            // 端口视图不支持直接覆盖内容：只能通过插入 / 抽出交互
            if (stack.isEmpty()) {
                backing.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public ItemStack insertItem(ItemStack stack, Action action, AutomationType automationType) {
            return mode == Mode.WASTE_INPUT ? backing.insertItem(stack, action, automationType) : stack;
        }

        @Override
        public ItemStack extractItem(int amount, Action action, AutomationType automationType) {
            if (mode == Mode.WASTE_INPUT) {
                return ItemStack.EMPTY;
            }
            if (mode == Mode.FUEL_ROD_OUTPUT && !isFuelRod(backing.getStack())) {
                return ItemStack.EMPTY;
            }
            return backing.extractItem(amount, action, automationType);
        }

        @Override
        public int getLimit(ItemStack stack) {
            return mode == Mode.WASTE_INPUT ? backing.getLimit(stack) : 0;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return mode == Mode.WASTE_INPUT && backing.isItemValid(stack);
        }

        @Nullable
        @Override
        public Slot createContainerSlot() {
            // 端口视图不参与 GUI / 容器同步
            return null;
        }

        @Override
        public void onContentsChanged() {
            backing.onContentsChanged();
        }

        @Override
        public CompoundTag serializeNBT(Provider provider) {
            return backing.serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(Provider provider, CompoundTag nbt) {
            backing.deserializeNBT(provider, nbt);
        }
    }
}
