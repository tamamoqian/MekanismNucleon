package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class MeknucFbrCache extends MultiblockCache<MeknucFbrMultiblockData> {

    private boolean active;
    private int insertion = 100;
    private double temperature = -1;
    private double power;
    private double xenon;
    private double burnProgress;
    private int rodsInCore;
    private int rodsDepleted;
    private int uraniumStock;
    private int plutoniumStock;
    private int breedProgress;
    private int claddingCount;
    private int volume;
    private int fuelAssemblies;
    private boolean rodsMelted;
    private boolean exploded;

    @Override
    public void merge(MultiblockCache<MeknucFbrMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucFbrCache other = (MeknucFbrCache) mergeCache;
        active = active || other.active;
        temperature = Math.max(temperature, other.temperature);
        power = Math.max(power, other.power);
        xenon = Math.max(xenon, other.xenon);
        burnProgress = Math.max(burnProgress, other.burnProgress);
        rodsInCore = Math.max(rodsInCore, other.rodsInCore);
        rodsDepleted = Math.max(rodsDepleted, other.rodsDepleted);
        uraniumStock = Math.max(uraniumStock, other.uraniumStock);
        plutoniumStock = Math.max(plutoniumStock, other.plutoniumStock);
        breedProgress = Math.max(breedProgress, other.breedProgress);
        rodsMelted = rodsMelted || other.rodsMelted;
        exploded = exploded || other.exploded;
    }

    @Override
    public void apply(Provider provider, MeknucFbrMultiblockData data) {
        super.apply(provider, data);
        data.setActive(active);
        data.restoreControlRodInsertion(insertion);
        if (temperature >= 0) {
            data.restoreTemperature(temperature);
        }
        data.setPower(power);
        data.setXenon(xenon);
        data.setBurnProgress(burnProgress);
        data.setRodsInCore(rodsInCore);
        data.setRodsDepleted(rodsDepleted);
        data.setUraniumStock(uraniumStock);
        data.setPlutoniumStock(plutoniumStock);
        data.setBreedProgress(breedProgress);
        if (claddingCount > 0) {
            data.setCladdingCount(claddingCount);
        }
        if (volume > 0) {
            data.setVolume(volume);
        }
        if (fuelAssemblies > 0) {
            data.setFuelAssemblies(fuelAssemblies);
        }
        data.setRodsMelted(rodsMelted);
        data.setExploded(exploded);
    }

    @Override
    public void sync(MeknucFbrMultiblockData data) {
        super.sync(data);
        active = data.isActive();
        insertion = data.getControlRodInsertion();
        temperature = data.getCoreTemperature();
        power = data.getPower();
        xenon = data.getXenon();
        burnProgress = data.getBurnProgress();
        rodsInCore = data.getRodsInCore();
        rodsDepleted = data.getRodsDepleted();
        uraniumStock = data.getUraniumStock();
        plutoniumStock = data.getPlutoniumStock();
        breedProgress = data.getBreedProgress();
        claddingCount = data.getCladdingCount();
        volume = data.getVolume();
        fuelAssemblies = data.getFuelAssemblies();
        rodsMelted = data.isRodsMelted();
        exploded = data.isExploded();
    }

    @Override
    public void load(Provider provider, CompoundTag tag) {
        super.load(provider, tag);
        active = tag.getBoolean("fbrActive");
        insertion = tag.getInt("fbrInsertion");
        temperature = tag.getDouble("fbrTemp");
        power = tag.getDouble("fbrPower");
        xenon = tag.getDouble("fbrXenon");
        burnProgress = tag.getDouble("fbrBurn");
        rodsInCore = tag.getInt("fbrRods");
        rodsDepleted = tag.getInt("fbrDepleted");
        uraniumStock = tag.getInt("fbrUranium");
        plutoniumStock = tag.getInt("fbrPlutonium");
        breedProgress = tag.getInt("fbrBreed");
        claddingCount = tag.getInt("fbrCladding");
        volume = tag.getInt("fbrVolume");
        fuelAssemblies = tag.getInt("fbrAssemblies");
        rodsMelted = tag.getBoolean("fbrMelted");
        exploded = tag.getBoolean("fbrExploded");
    }

    @Override
    public void save(Provider provider, CompoundTag tag) {
        super.save(provider, tag);
        tag.putBoolean("fbrActive", active);
        tag.putInt("fbrInsertion", insertion);
        tag.putDouble("fbrTemp", temperature);
        tag.putDouble("fbrPower", power);
        tag.putDouble("fbrXenon", xenon);
        tag.putDouble("fbrBurn", burnProgress);
        tag.putInt("fbrRods", rodsInCore);
        tag.putInt("fbrDepleted", rodsDepleted);
        tag.putInt("fbrUranium", uraniumStock);
        tag.putInt("fbrPlutonium", plutoniumStock);
        tag.putInt("fbrBreed", breedProgress);
        tag.putInt("fbrCladding", claddingCount);
        tag.putInt("fbrVolume", volume);
        tag.putInt("fbrAssemblies", fuelAssemblies);
        tag.putBoolean("fbrMelted", rodsMelted);
        tag.putBoolean("fbrExploded", exploded);
    }
}
