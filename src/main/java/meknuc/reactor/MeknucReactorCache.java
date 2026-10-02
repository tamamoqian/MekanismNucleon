package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class MeknucReactorCache extends MultiblockCache<MeknucReactorMultiblockData> {

    private boolean active;
    private int controlRodInsertion;
    private int burnTime;

    @Override
    public void merge(MultiblockCache<MeknucReactorMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucReactorCache other = (MeknucReactorCache) mergeCache;
        active = active || other.active;
        controlRodInsertion = Math.max(controlRodInsertion, other.controlRodInsertion);
        burnTime = Math.max(burnTime, other.burnTime);
    }

    @Override
    public void apply(Provider provider, MeknucReactorMultiblockData data) {
        super.apply(provider, data);
        data.setActive(active);
        data.setControlRodInsertion(controlRodInsertion);
        data.setBurnTime(burnTime);
    }

    @Override
    public void sync(MeknucReactorMultiblockData data) {
        super.sync(data);
        active = data.isActive();
        controlRodInsertion = data.getControlRodInsertion();
        burnTime = data.getBurnTime();
    }

    @Override
    public void load(Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);
        active = nbtTags.getBoolean("active");
        NBTUtils.setIntIfPresent(nbtTags, "control_rod_insertion", value -> controlRodInsertion = value);
        NBTUtils.setIntIfPresent(nbtTags, "burn_time", value -> burnTime = value);
    }

    @Override
    public void save(Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putBoolean("active", active);
        nbtTags.putInt("control_rod_insertion", controlRodInsertion);
        nbtTags.putInt("burn_time", burnTime);
    }
}
