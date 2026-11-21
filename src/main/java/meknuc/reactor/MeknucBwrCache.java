package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MeknucBwrCache extends MultiblockCache<MeknucBwrMultiblockData> {

    private boolean active;
    private boolean autoStopOnFuelExhausted = true;
    private int controlRodInsertion = -1;
    private int burnTime;
    private double damage;
    private int meltdownTimer = -1;
    private boolean meltedDown;
    private double temperature = -1.0;
    private ItemStack runningProduct = ItemStack.EMPTY;
    private int inCoreCount;

    @Override
    public void merge(MultiblockCache<MeknucBwrMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucBwrCache other = (MeknucBwrCache) mergeCache;
        active = active || other.active;
        controlRodInsertion = Math.max(controlRodInsertion, other.controlRodInsertion);
        burnTime = Math.max(burnTime, other.burnTime);
        damage = Math.max(damage, other.damage);
        meltdownTimer = Math.max(meltdownTimer, other.meltdownTimer);
        meltedDown = meltedDown || other.meltedDown;
        temperature = Math.max(temperature, other.temperature);
        if (runningProduct.isEmpty()) {
            runningProduct = other.runningProduct;
            inCoreCount = other.inCoreCount;
        }
    }

    @Override
    public void apply(Provider provider, MeknucBwrMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        if (getFluidTanks(null).size() != data.getFluidTanks((Direction) null).size()) {
            getFluidTanks(null).clear();
        }
        super.apply(provider, data);
        data.setActive(active);
        data.restoreAutoStopOnFuelExhausted(autoStopOnFuelExhausted);
        if (controlRodInsertion >= 0) {
            data.restoreControlRodInsertion(controlRodInsertion);
        }
        data.setBurnTime(burnTime);
        data.setDamage(damage);
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
    }

    @Override
    public void sync(MeknucBwrMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        if (getFluidTanks(null).size() != data.getFluidTanks((Direction) null).size()) {
            getFluidTanks(null).clear();
        }
        super.sync(data);
        active = data.isActive();
        autoStopOnFuelExhausted = data.isAutoStopOnFuelExhausted();
        controlRodInsertion = data.getControlRodInsertion();
        burnTime = data.getBurnTime();
        damage = data.getDamage();
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
        NBTUtils.setIntIfPresent(nbtTags, "burn_time", value -> burnTime = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "damage", value -> damage = value);
        NBTUtils.setIntIfPresent(nbtTags, "meltdown_timer", value -> meltdownTimer = value);
        meltedDown = nbtTags.getBoolean("melted_down");
        NBTUtils.setDoubleIfPresent(nbtTags, "temperature", value -> temperature = value);
        runningProduct = ItemStack.EMPTY;
        inCoreCount = 0;
        if (nbtTags.contains("running_product")) {
            CompoundTag product = nbtTags.getCompound("running_product").copy();
            int storedCount = product.contains("count") ? product.getInt("count") : 1;
            product.putInt("count",
                  Math.min(Math.max(storedCount, 1), MeknucBwrMultiblockData.SLOT_STACK_LIMIT));
            ItemStack parsed = ItemStack.parse(provider, product).orElse(ItemStack.EMPTY);
            if (!parsed.isEmpty()) {
                runningProduct = parsed.copyWithCount(1);
                inCoreCount = Math.max(storedCount, 0);
            }
        }
        NBTUtils.setIntIfPresent(nbtTags, "in_core_count", value -> inCoreCount = value);
    }

    @Override
    public void save(Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putBoolean("active", active);
        nbtTags.putBoolean("auto_stop_on_fuel_exhausted", autoStopOnFuelExhausted);
        nbtTags.putInt("control_rod_insertion", controlRodInsertion);
        nbtTags.putInt("burn_time", burnTime);
        nbtTags.putDouble("damage", damage);
        nbtTags.putInt("meltdown_timer", meltdownTimer);
        nbtTags.putBoolean("melted_down", meltedDown);
        nbtTags.putDouble("temperature", temperature);
        if (!runningProduct.isEmpty()) {
            nbtTags.put("running_product", runningProduct.save(provider));
        }
        nbtTags.putInt("in_core_count", inCoreCount);
    }
}
