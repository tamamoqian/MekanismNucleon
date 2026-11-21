package meknuc.reactor;

import mekanism.common.util.NBTUtils;
import meknuc.items.MeknucRodType;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class MeknucPwrCache extends MeknucReactorCache<MeknucPwrMultiblockData> {

    private boolean coolantActive;
    private boolean rodsFailed;
    private double powerLevel;
    private double burnProgress;
    private int batchBaseTicks;

    @Override
    protected void mergeExtra(MeknucReactorCache<MeknucPwrMultiblockData> other) {
        MeknucPwrCache cache = (MeknucPwrCache) other;
        coolantActive = coolantActive || cache.coolantActive;
        rodsFailed = rodsFailed || cache.rodsFailed;
        powerLevel = Math.max(powerLevel, cache.powerLevel);
    }

    @Override
    protected void mergeRunningProductExtra(MeknucReactorCache<MeknucPwrMultiblockData> other) {
        MeknucPwrCache cache = (MeknucPwrCache) other;
        burnProgress = cache.burnProgress;
        batchBaseTicks = cache.batchBaseTicks;
    }

    @Override
    protected void applyExtra(MeknucPwrMultiblockData data) {
        data.setCoolantOn(coolantActive);
        data.setRodsFailed(rodsFailed);
        data.setPowerLevel(powerLevel);
        data.setBurnProgress(burnProgress);
        data.setBatchBaseTicks(batchBaseTicks);
    }

    @Override
    protected void afterApply(MeknucPwrMultiblockData data) {
        data.refreshBurnTimeDisplay();
    }

    @Override
    protected void syncExtra(MeknucPwrMultiblockData data) {
        coolantActive = data.isCoolantOn();
        rodsFailed = data.isRodsFailed();
        powerLevel = data.getPowerLevel();
        burnProgress = data.getBurnProgress();
        batchBaseTicks = data.getBatchBaseTicks();
    }

    @Override
    protected void loadExtra(CompoundTag nbtTags) {
        NBTUtils.setBooleanIfPresent(nbtTags, "coolant_active", value -> coolantActive = value);
        NBTUtils.setBooleanIfPresent(nbtTags, "rods_failed", value -> rodsFailed = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "power_level", value -> powerLevel = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "burn_progress", value -> burnProgress = value);
        NBTUtils.setIntIfPresent(nbtTags, "batch_base_ticks", value -> batchBaseTicks = value);
    }

    @Override
    protected double convertLoadedTemperature(double value) {
        return value > 200.0 ? value - 273.15 : value;
    }

    @Override
    protected void afterLoad() {
        if (batchBaseTicks <= 0 && !runningProduct.isEmpty()) {
            MeknucRodType type = MeknucPwrFuels.get(runningProduct);
            if (type != null) {
                batchBaseTicks = type.burnTime();
            }
        }
    }

    @Override
    protected void saveExtra(Provider provider, CompoundTag nbtTags) {
        nbtTags.putBoolean("coolant_active", coolantActive);
        nbtTags.putBoolean("rods_failed", rodsFailed);
        nbtTags.putDouble("power_level", powerLevel);
        nbtTags.putDouble("burn_progress", burnProgress);
        nbtTags.putInt("batch_base_ticks", batchBaseTicks);
    }
}
