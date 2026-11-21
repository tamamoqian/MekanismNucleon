package meknuc.reactor.spentpool;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockCache;
import meknuc.items.MeknucRodType;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 乏燃料冷却池缓存：除常规的库存/流体/能量外，额外保存正在冷却的燃料棒线程进度，
 * 使结构拆解重组或存档重载后进度不丢失。
 *
 * <p>另外接管多方块成型时的缓存合并溢出：燃料棒是放射性的，一旦被 Mekanism 的
 * {@code FormationProtocol} 当成 {@code rejectedItems} 丢到玩家脚下，5 分钟超时清理时
 * 会触发 {@code MeknucRadiation.detonate}（爆炸 + 辐射源）。这里把溢出留在缓存里，
 * 等下一次成型再塞回空槽。只在成型/读档时执行，没有每 tick 开销。</p>
 */
public class MeknucSpentFuelPoolCache extends MultiblockCache<MeknucSpentFuelPoolMultiblockData> {

    private static final String THREADS_TAG = "cooling_threads";
    private static final String OVERFLOW_TAG = "overflow_items";

    /** 当前版本的槽位总数（输入缓存 + 输出缓存），用于对齐旧存档缓存。 */
    private static final int SLOT_COUNT = MeknucSpentFuelPoolConstants.INPUT_SLOT_COUNT
          + MeknucSpentFuelPoolConstants.MAX_COOLED_RODS;

    private List<MeknucSpentFuelPoolMultiblockData.RodThread> threads = new ArrayList<>();
    /** 合并/迁移时装不下的燃料棒，等下次成型再放回槽位；绝不丢弃。 */
    private final List<ItemStack> overflow = new ArrayList<>();

    @Override
    public void apply(Provider provider, MeknucSpentFuelPoolMultiblockData data) {
        // 基类 apply 按下标把缓存槽位复制进数据槽位，长度不一致会错位；
        // 成型时缓存里可能还存着旧版本的 68 个槽位，先按新布局重建并搬运旧内容。
        rebuildCacheSlots(data, true);
        super.apply(provider, data);
        data.restoreThreads(threads);
        drainOverflow(data);
    }

    @Override
    public void sync(MeknucSpentFuelPoolMultiblockData data) {
        // sync 是“以数据为准”写回缓存：长度不一致时重建空槽位即可，
        // 否则基类会按新数据的 128 个槽位去索引旧缓存的 68 个槽位而越界崩溃。
        rebuildCacheSlots(data, false);
        super.sync(data);
        this.threads = data.copyThreads();
    }

    /**
     * 按当前数据的槽位数重建缓存的物品槽位列表，只动物品槽，
     * 不会碰缓存里的水/蒸汽等其它容器。
     *
     * @param keepLegacy 是否保留旧槽位里的物品（结构未成型时缓存是唯一副本，需要保留）
     */
    private void rebuildCacheSlots(MeknucSpentFuelPoolMultiblockData data, boolean keepLegacy) {
        rebuildCacheSlots(data.getSlotCount(), keepLegacy);
    }

    private void rebuildCacheSlots(int expected, boolean keepLegacy) {
        List<IInventorySlot> cached = getInventorySlots(null);
        if (cached.size() == expected) {
            return;
        }
        List<ItemStack> legacy = new ArrayList<>(cached.size());
        if (keepLegacy) {
            for (IInventorySlot slot : cached) {
                legacy.add(slot.getStack().copy());
            }
        }
        cached.clear();
        for (int i = 0; i < expected; i++) {
            cached.add(BasicInventorySlot.at(this, 0, 0));
        }
        if (keepLegacy) {
            restoreLegacyStacks(cached, legacy);
        }
    }

    private void restoreLegacyStacks(List<IInventorySlot> slots, List<ItemStack> legacy) {
        if (legacy.isEmpty()) {
            return;
        }
        // 旧布局固定为“若干输入槽 + 64 个输出缓存槽”
        int legacyInput = Math.max(0, legacy.size() - MeknucSpentFuelPoolConstants.MAX_COOLED_RODS);
        for (int i = 0; i < legacy.size(); i++) {
            ItemStack stack = legacy.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            int target = i < legacyInput
                  ? i
                  : MeknucSpentFuelPoolConstants.INPUT_SLOT_COUNT + (i - legacyInput);
            if (target >= 0 && target < slots.size()) {
                IInventorySlot slot = slots.get(target);
                if (slot.isEmpty()) {
                    placeUnchecked(slot, stack);
                    continue;
                }
            }
            // 放不下就留在溢出列表，不能静默销毁（燃料棒是放射性的）
            overflow.add(stack);
        }
    }

    @Override
    public void merge(MultiblockCache<MeknucSpentFuelPoolMultiblockData> mergeCache, RejectContents rejectContents) {
        // 基类 merge 用 StackUtils.merge 按下标合并，两个缓存的槽位数不一致会抛异常；
        // 旧存档缓存可能还是 68 个槽位，先把两边都对齐到当前布局。
        rebuildCacheSlots(SLOT_COUNT, true);
        if (mergeCache instanceof MeknucSpentFuelPoolCache cache) {
            cache.rebuildCacheSlots(SLOT_COUNT, true);
        }
        // 关键：用私有的 RejectContents 接住溢出，让调用方拿到的列表始终为空，
        // 这样 FormationProtocol 不会把装不下的燃料棒丢到世界（5 分钟后会引爆）。
        RejectContents internal = new RejectContents();
        super.merge(mergeCache, internal);
        absorb(internal.rejectedItems);
        if (mergeCache instanceof MeknucSpentFuelPoolCache cache) {
            List<MeknucSpentFuelPoolMultiblockData.RodThread> merged = new ArrayList<>(this.threads);
            merged.addAll(cache.threads);
            this.threads = merged;
            absorb(cache.overflow);
            cache.overflow.clear();
        }
    }

    private void absorb(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                overflow.add(stack.copy());
            }
        }
    }

    /** 成型时把溢出列表塞回数据槽位：热燃料棒进输入缓存，冷却完成的进输出缓存。 */
    private void drainOverflow(MeknucSpentFuelPoolMultiblockData data) {
        if (overflow.isEmpty()) {
            return;
        }
        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack stack : overflow) {
            if (stack.isEmpty()) {
                continue;
            }
            List<IInventorySlot> targets = MeknucSpentFuelPoolMultiblockData.isAcceptedFuel(stack)
                  ? data.getInputSlots()
                  : data.getOutputSlots();
            boolean placed = false;
            for (IInventorySlot slot : targets) {
                if (slot.isEmpty()) {
                    placeUnchecked(slot, stack);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                remaining.add(stack);
            }
        }
        overflow.clear();
        overflow.addAll(remaining);
    }

    private static void placeUnchecked(IInventorySlot slot, ItemStack stack) {
        if (slot instanceof BasicInventorySlot basic) {
            basic.setStackUnchecked(stack);
        } else {
            slot.setStack(stack);
        }
    }

    @Override
    public void load(Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);
        this.threads = new ArrayList<>();
        if (nbtTags.contains(THREADS_TAG, 9)) {
            ListTag list = nbtTags.getList(THREADS_TAG, 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                MeknucRodType type = MeknucRodType.byId(tag.getString("type"));
                if (type != null) {
                    this.threads.add(new MeknucSpentFuelPoolMultiblockData.RodThread(type, tag.getInt("progress")));
                }
            }
        }
        this.overflow.clear();
        if (nbtTags.contains(OVERFLOW_TAG, 9)) {
            ListTag list = nbtTags.getList(OVERFLOW_TAG, 10);
            for (int i = 0; i < list.size(); i++) {
                ItemStack stack = ItemStack.parse(provider, list.getCompound(i)).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    this.overflow.add(stack);
                }
            }
        }
    }

    @Override
    public void save(Provider provider, @NotNull CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        ListTag list = new ListTag();
        for (MeknucSpentFuelPoolMultiblockData.RodThread thread : threads) {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", thread.getType().id());
            tag.putInt("progress", thread.getProgress());
            list.add(tag);
        }
        nbtTags.put(THREADS_TAG, list);
        if (!overflow.isEmpty()) {
            ListTag overflowTag = new ListTag();
            for (ItemStack stack : overflow) {
                if (!stack.isEmpty()) {
                    overflowTag.add(stack.save(provider));
                }
            }
            nbtTags.put(OVERFLOW_TAG, overflowTag);
        }
    }
}
