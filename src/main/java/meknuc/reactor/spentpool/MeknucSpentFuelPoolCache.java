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
 */
public class MeknucSpentFuelPoolCache extends MultiblockCache<MeknucSpentFuelPoolMultiblockData> {

    private static final String THREADS_TAG = "cooling_threads";

    /** 当前版本的槽位总数（输入缓存 + 输出缓存），用于对齐旧存档缓存。 */
    private static final int SLOT_COUNT = MeknucSpentFuelPoolMultiblockData.INPUT_SLOT_COUNT
          + MeknucSpentFuelPoolMultiblockData.MAX_COOLED_RODS;

    private List<MeknucSpentFuelPoolMultiblockData.RodThread> threads = new ArrayList<>();

    @Override
    public void apply(Provider provider, MeknucSpentFuelPoolMultiblockData data) {
        // 基类 apply 按下标把缓存槽位复制进数据槽位，长度不一致会错位；
        // 成型时缓存里可能还存着旧版本的 68 个槽位，先按新布局重建并搬运旧内容。
        rebuildCacheSlots(data, true);
        super.apply(provider, data);
        data.restoreThreads(threads);
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
                legacy.add(slot.getStack());
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

    private static void restoreLegacyStacks(List<IInventorySlot> slots, List<ItemStack> legacy) {
        if (legacy.isEmpty()) {
            return;
        }
        // 旧布局固定为“若干输入槽 + 64 个输出缓存槽”
        int legacyInput = Math.max(0, legacy.size() - MeknucSpentFuelPoolMultiblockData.MAX_COOLED_RODS);
        for (int i = 0; i < legacy.size(); i++) {
            ItemStack stack = legacy.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            int target = i < legacyInput
                  ? i
                  : MeknucSpentFuelPoolMultiblockData.INPUT_SLOT_COUNT + (i - legacyInput);
            if (target < 0 || target >= slots.size()) {
                continue;
            }
            IInventorySlot slot = slots.get(target);
            if (!slot.isEmpty()) {
                continue;
            }
            if (slot instanceof BasicInventorySlot basic) {
                basic.setStackUnchecked(stack);
            } else {
                slot.setStack(stack);
            }
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
        super.merge(mergeCache, rejectContents);
        if (mergeCache instanceof MeknucSpentFuelPoolCache cache) {
            List<MeknucSpentFuelPoolMultiblockData.RodThread> merged = new ArrayList<>(this.threads);
            merged.addAll(cache.threads);
            this.threads = merged;
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
    }
}
