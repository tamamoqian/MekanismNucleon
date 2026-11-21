package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class MeknucReactorCache<T extends MeknucReactorMultiblockData>
      extends MultiblockCache<T> {

    protected boolean active;
    protected boolean autoStopOnFuelExhausted = true;
    protected int controlRodInsertion = -1;
    protected int meltdownTimer = -1;
    protected boolean meltedDown;
    protected double temperature = -1.0;
    protected ItemStack runningProduct = ItemStack.EMPTY;
    protected int inCoreCount;

    @Override
    public void merge(MultiblockCache<T> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucReactorCache<T> other = (MeknucReactorCache<T>) mergeCache;
        active = active || other.active;
        autoStopOnFuelExhausted = autoStopOnFuelExhausted || other.autoStopOnFuelExhausted;
        controlRodInsertion = Math.max(controlRodInsertion, other.controlRodInsertion);
        meltdownTimer = Math.max(meltdownTimer, other.meltdownTimer);
        meltedDown = meltedDown || other.meltedDown;
        temperature = Math.max(temperature, other.temperature);
        mergeExtra(other);
        if (runningProduct.isEmpty()) {
            runningProduct = other.runningProduct;
            inCoreCount = other.inCoreCount;
            mergeRunningProductExtra(other);
        }
    }

    @Override
    public void apply(Provider provider, T data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        prepareTanks(data);
        super.apply(provider, data);
        data.setActive(active);
        data.restoreAutoStopOnFuelExhausted(autoStopOnFuelExhausted);
        if (controlRodInsertion >= 0) {
            data.restoreControlRodInsertion(controlRodInsertion);
        }
        applyExtra(data);
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
        afterApply(data);
    }

    @Override
    public void sync(T data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        prepareTanks(data);
        super.sync(data);
        active = data.isActive();
        autoStopOnFuelExhausted = data.isAutoStopOnFuelExhausted();
        controlRodInsertion = data.getControlRodInsertion();
        syncExtra(data);
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
        loadExtra(nbtTags);
        NBTUtils.setIntIfPresent(nbtTags, "meltdown_timer", value -> meltdownTimer = value);
        meltedDown = nbtTags.getBoolean("melted_down");
        NBTUtils.setDoubleIfPresent(nbtTags, "temperature",
              value -> temperature = convertLoadedTemperature(value));
        runningProduct = ItemStack.EMPTY;
        inCoreCount = 0;
        if (nbtTags.contains("running_product")) {
            CompoundTag product = nbtTags.getCompound("running_product").copy();
            int storedCount = product.contains("count") ? product.getInt("count") : 1;
            product.putInt("count",
                  Math.min(Math.max(storedCount, 1), MeknucReactorConstants.SLOT_STACK_LIMIT));
            ItemStack parsed = ItemStack.parse(provider, product).orElse(ItemStack.EMPTY);
            if (!parsed.isEmpty()) {
                runningProduct = parsed.copyWithCount(1);
                inCoreCount = Math.max(storedCount, 0);
            }
        }
        NBTUtils.setIntIfPresent(nbtTags, "in_core_count", value -> inCoreCount = value);
        afterLoad();
    }

    @Override
    public void save(Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putBoolean("active", active);
        nbtTags.putBoolean("auto_stop_on_fuel_exhausted", autoStopOnFuelExhausted);
        nbtTags.putInt("control_rod_insertion", controlRodInsertion);
        saveExtra(provider, nbtTags);
        nbtTags.putInt("meltdown_timer", meltdownTimer);
        nbtTags.putBoolean("melted_down", meltedDown);
        nbtTags.putDouble("temperature", temperature);
        if (!runningProduct.isEmpty()) {
            nbtTags.put("running_product", runningProduct.save(provider));
        }
        nbtTags.putInt("in_core_count", inCoreCount);
    }

    protected void mergeExtra(MeknucReactorCache<T> other) {
    }

    protected void mergeRunningProductExtra(MeknucReactorCache<T> other) {
    }

    protected void prepareTanks(T data) {
    }

    protected void applyExtra(T data) {
    }

    protected void afterApply(T data) {
    }

    protected void syncExtra(T data) {
    }

    protected void loadExtra(CompoundTag nbtTags) {
    }

    protected double convertLoadedTemperature(double value) {
        return value;
    }

    protected void afterLoad() {
    }

    protected void saveExtra(Provider provider, CompoundTag nbtTags) {
    }
}
