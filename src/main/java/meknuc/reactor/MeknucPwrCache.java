package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import mekanism.common.util.NBTUtils;
import meknuc.items.MeknucRodType;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MeknucPwrCache extends MultiblockCache<MeknucPwrMultiblockData> {

    private boolean active;
    private boolean autoStopOnFuelExhausted = true;
    private int controlRodInsertion = -1;
    private boolean coolantActive;
    private boolean rodsFailed;
    private double powerLevel;
    private double burnProgress;
    private int batchBaseTicks;
    private int meltdownTimer = -1;
    private boolean meltedDown;
    private double temperature = -1.0;
    private ItemStack runningProduct = ItemStack.EMPTY;
    private int inCoreCount;

    @Override
    public void merge(MultiblockCache<MeknucPwrMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucPwrCache other = (MeknucPwrCache) mergeCache;
        active = active || other.active;
        autoStopOnFuelExhausted = autoStopOnFuelExhausted || other.autoStopOnFuelExhausted;
        controlRodInsertion = Math.max(controlRodInsertion, other.controlRodInsertion);
        coolantActive = coolantActive || other.coolantActive;
        rodsFailed = rodsFailed || other.rodsFailed;
        powerLevel = Math.max(powerLevel, other.powerLevel);
        meltdownTimer = Math.max(meltdownTimer, other.meltdownTimer);
        meltedDown = meltedDown || other.meltedDown;
        temperature = Math.max(temperature, other.temperature);
        if (runningProduct.isEmpty()) {
            runningProduct = other.runningProduct;
            inCoreCount = other.inCoreCount;
            burnProgress = other.burnProgress;
            batchBaseTicks = other.batchBaseTicks;
        }
    }

    @Override
    public void apply(Provider provider, MeknucPwrMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        super.apply(provider, data);
        data.setActive(active);
        data.restoreAutoStopOnFuelExhausted(autoStopOnFuelExhausted);
        if (controlRodInsertion >= 0) {
            data.restoreControlRodInsertion(controlRodInsertion);
        }
        data.setCoolantOn(coolantActive);
        data.setRodsFailed(rodsFailed);
        data.setPowerLevel(powerLevel);
        data.setBurnProgress(burnProgress);
        data.setBatchBaseTicks(batchBaseTicks);
        if (meltdownTimer >= 0) {
            data.setMeltdownTimer(meltdownTimer);
        }
        if (meltedDown) {
            data.setMeltedDown(true);
        }
        if (temperature >= 0.0) {
            data.restoreTemperature(temperature);
        }
        data.setRunningProduct(runningProduct);
        data.setInCoreCount(inCoreCount);
        data.refreshBurnTimeDisplay();
    }

    @Override
    public void sync(MeknucPwrMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        super.sync(data);
        active = data.isActive();
        autoStopOnFuelExhausted = data.isAutoStopOnFuelExhausted();
        controlRodInsertion = data.getControlRodInsertion();
        coolantActive = data.isCoolantOn();
        rodsFailed = data.isRodsFailed();
        powerLevel = data.getPowerLevel();
        burnProgress = data.getBurnProgress();
        batchBaseTicks = data.getBatchBaseTicks();
        meltdownTimer = data.getMeltdownTimer();
        meltedDown = data.isMeltedDown();
        temperature = data.getTemperature();
        runningProduct = data.getRunningProduct();
        inCoreCount = data.getInCoreCount();
    }

    @Override
    public void load(Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);
        active = nbtTags.getBoolean("active");
        NBTUtils.setBooleanIfPresent(nbtTags, "auto_stop_on_fuel_exhausted",
              value -> autoStopOnFuelExhausted = value);
        NBTUtils.setIntIfPresent(nbtTags, "control_rod_insertion", value -> controlRodInsertion = value);
        NBTUtils.setBooleanIfPresent(nbtTags, "coolant_active", value -> coolantActive = value);
        NBTUtils.setBooleanIfPresent(nbtTags, "rods_failed", value -> rodsFailed = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "power_level", value -> powerLevel = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "burn_progress", value -> burnProgress = value);
        NBTUtils.setIntIfPresent(nbtTags, "batch_base_ticks", value -> batchBaseTicks = value);
        NBTUtils.setIntIfPresent(nbtTags, "meltdown_timer", value -> meltdownTimer = value);
        meltedDown = nbtTags.getBoolean("melted_down");
        NBTUtils.setDoubleIfPresent(nbtTags, "temperature", value -> temperature = value > 200.0 ? value - 273.15 : value);
        runningProduct = ItemStack.EMPTY;
        inCoreCount = 0;
        if (nbtTags.contains("running_product")) {
            CompoundTag product = nbtTags.getCompound("running_product").copy();
            int storedCount = product.contains("count") ? product.getInt("count") : 1;
            product.putInt("count",
                  Math.min(Math.max(storedCount, 1), MeknucPwrMultiblockData.SLOT_STACK_LIMIT));
            ItemStack parsed = ItemStack.parse(provider, product).orElse(ItemStack.EMPTY);
            if (!parsed.isEmpty()) {
                runningProduct = parsed.copyWithCount(1);
                inCoreCount = Math.max(storedCount, 0);
            }
        }
        NBTUtils.setIntIfPresent(nbtTags, "in_core_count", value -> inCoreCount = value);
        if (batchBaseTicks <= 0 && !runningProduct.isEmpty()) {
            MeknucRodType type = MeknucPwrFuels.get(runningProduct);
            if (type != null) {
                batchBaseTicks = type.burnTime();
            }
        }
    }

    @Override
    public void save(Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putBoolean("active", active);
        nbtTags.putBoolean("auto_stop_on_fuel_exhausted", autoStopOnFuelExhausted);
        nbtTags.putInt("control_rod_insertion", controlRodInsertion);
        nbtTags.putBoolean("coolant_active", coolantActive);
        nbtTags.putBoolean("rods_failed", rodsFailed);
        nbtTags.putDouble("power_level", powerLevel);
        nbtTags.putDouble("burn_progress", burnProgress);
        nbtTags.putInt("batch_base_ticks", batchBaseTicks);
        nbtTags.putInt("meltdown_timer", meltdownTimer);
        nbtTags.putBoolean("melted_down", meltedDown);
        nbtTags.putDouble("temperature", temperature);
        if (!runningProduct.isEmpty()) {
            nbtTags.put("running_product", runningProduct.save(provider));
        }
        nbtTags.putInt("in_core_count", inCoreCount);
    }
}
