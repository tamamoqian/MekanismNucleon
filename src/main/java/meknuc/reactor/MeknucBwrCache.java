package meknuc.reactor;

import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class MeknucBwrCache extends MeknucReactorCache<MeknucBwrMultiblockData> {

    private int burnTime;
    private double damage;
    private double lastFissionPower;
    private double lastVoidFraction;

    @Override
    protected void mergeExtra(MeknucReactorCache<MeknucBwrMultiblockData> other) {
        MeknucBwrCache cache = (MeknucBwrCache) other;
        burnTime = Math.max(burnTime, cache.burnTime);
        damage = Math.max(damage, cache.damage);
        lastFissionPower = Math.max(lastFissionPower, cache.lastFissionPower);
        lastVoidFraction = Math.max(lastVoidFraction, cache.lastVoidFraction);
    }

    @Override
    protected void prepareTanks(MeknucBwrMultiblockData data) {
        if (getFluidTanks(null).size() != data.getFluidTanks((Direction) null).size()) {
            getFluidTanks(null).clear();
        }
    }

    @Override
    protected void applyExtra(MeknucBwrMultiblockData data) {
        data.setBurnTime(burnTime);
        data.setDamage(damage);
        data.setLastFissionPower(lastFissionPower);
        data.setLastVoidFraction(lastVoidFraction);
    }

    @Override
    protected void syncExtra(MeknucBwrMultiblockData data) {
        burnTime = data.getBurnTime();
        damage = data.getDamage();
        lastFissionPower = data.getLastFissionPower();
        lastVoidFraction = data.getLastVoidFraction();
    }

    @Override
    protected void loadExtra(CompoundTag nbtTags) {
        NBTUtils.setIntIfPresent(nbtTags, "burn_time", value -> burnTime = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "damage", value -> damage = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "last_power", value -> lastFissionPower = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "last_void", value -> lastVoidFraction = value);
    }

    @Override
    protected void saveExtra(Provider provider, CompoundTag nbtTags) {
        nbtTags.putInt("burn_time", burnTime);
        nbtTags.putDouble("damage", damage);
        nbtTags.putDouble("last_power", lastFissionPower);
        nbtTags.putDouble("last_void", lastVoidFraction);
    }
}
