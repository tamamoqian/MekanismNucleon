package meknuc.wastestorage;

import java.util.Arrays;
import mekanism.common.lib.multiblock.MultiblockCache;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * 放射性废料存储罐的多方块缓存：除了常规的槽位内容外，额外保存并行衰变线程（配方 + 进度），
 * 让结构拆解重组或存档重载后进度不丢失。
 * <p>
 * 旧版本存档只保存了五条配方各一个进度（{@code decay_progress}），
 * 读档时会自动转换成线程，老存档进度不会丢。
 */
public class MeknucWasteStorageCache extends MultiblockCache<MeknucWasteStorageMultiblockData> {

    /** 线程打包数据：{@code [recipe, progress, recipe, progress, ...]}。 */
    private static final String THREADS_TAG = "decay_threads";
    /** 旧版本存档的兼容标签：五条配方各一个进度。 */
    private static final String LEGACY_PROGRESS_TAG = "decay_progress";

    private int[] threads = new int[0];

    @Override
    public void apply(Provider provider, MeknucWasteStorageMultiblockData data) {
        super.apply(provider, data);
        data.restoreThreads(threads);
    }

    @Override
    public void sync(MeknucWasteStorageMultiblockData data) {
        super.sync(data);
        this.threads = data.copyThreads();
    }

    @Override
    public void merge(MultiblockCache<MeknucWasteStorageMultiblockData> mergeCache,
          MultiblockCache.RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        if (mergeCache instanceof MeknucWasteStorageCache cache) {
            int[] merged = Arrays.copyOf(threads, threads.length + cache.threads.length);
            System.arraycopy(cache.threads, 0, merged, threads.length, cache.threads.length);
            this.threads = merged;
        }
    }

    @Override
    public void load(Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);
        if (nbtTags.contains(THREADS_TAG)) {
            this.threads = nbtTags.getIntArray(THREADS_TAG);
            return;
        }
        // 旧存档：五条配方各存了一个进度，转换成一条线程
        int[] legacy = nbtTags.getIntArray(LEGACY_PROGRESS_TAG);
        int[] converted = new int[legacy.length * 2];
        int size = 0;
        for (int i = 0; i < legacy.length; i++) {
            if (legacy[i] > 0) {
                converted[size++] = i;
                converted[size++] = legacy[i];
            }
        }
        this.threads = Arrays.copyOf(converted, size);
    }

    @Override
    public void save(Provider provider, @NotNull CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putIntArray(THREADS_TAG, threads);
    }
}
