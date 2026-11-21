package meknuc.particle;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockCache.RejectContents;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

/**
 * 粒子加速器的多方块缓存：除默认的能量 / 化学品 / 物品外，
 * 还保存自动检测到的配方、工作模式、进度与状态，拆装结构时不会丢失。
 */
public class MeknucParticleAcceleratorCache extends MultiblockCache<MeknucParticleAcceleratorMultiblockData> {

    private int activeRecipe = -1;
    private int mode = MeknucParticleAcceleratorRecipes.MODE_ACCELERATION;
    private int progressTicks;
    private int recipeDuration;
    private double activeEnergyPerTick;
    private int status;

    @Override
    public void merge(MultiblockCache<MeknucParticleAcceleratorMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        MeknucParticleAcceleratorCache other = (MeknucParticleAcceleratorCache) mergeCache;
        if (activeRecipe < 0) {
            activeRecipe = other.activeRecipe;
        }
        if (mode == MeknucParticleAcceleratorRecipes.MODE_ACCELERATION) {
            mode = other.mode;
        }
        progressTicks = Math.max(progressTicks, other.progressTicks);
        recipeDuration = Math.max(recipeDuration, other.recipeDuration);
        activeEnergyPerTick = Math.max(activeEnergyPerTick, other.activeEnergyPerTick);
        if (status == MeknucParticleAcceleratorMultiblockData.STATUS_IDLE) {
            status = other.status;
        }
    }

    @Override
    public void apply(Provider provider, MeknucParticleAcceleratorMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        if (getInventorySlots(null).size() != data.getInventorySlots((Direction) null).size()) {
            getInventorySlots(null).clear();
        }
        super.apply(provider, data);
        data.restoreState(activeRecipe, progressTicks, recipeDuration, activeEnergyPerTick, status, mode);
    }

    @Override
    public void sync(MeknucParticleAcceleratorMultiblockData data) {
        if (getChemicalTanks(null).size() != data.getChemicalTanks((Direction) null).size()) {
            getChemicalTanks(null).clear();
        }
        if (getInventorySlots(null).size() != data.getInventorySlots((Direction) null).size()) {
            getInventorySlots(null).clear();
        }
        super.sync(data);
        activeRecipe = data.getActiveRecipeIndex();
        mode = data.getMode();
        progressTicks = data.getProgressTicks();
        recipeDuration = data.getRecipeDuration();
        activeEnergyPerTick = data.getActiveEnergyPerTick();
        status = data.getStatus();
    }

    @Override
    public void load(Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);
        NBTUtils.setIntIfPresent(nbtTags, "active_recipe", value -> activeRecipe = value);
        NBTUtils.setIntIfPresent(nbtTags, "selected_recipe", value -> activeRecipe = value);
        NBTUtils.setIntIfPresent(nbtTags, "mode", value -> mode = value);
        NBTUtils.setIntIfPresent(nbtTags, "progress_ticks", value -> progressTicks = value);
        NBTUtils.setIntIfPresent(nbtTags, "recipe_duration", value -> recipeDuration = value);
        NBTUtils.setDoubleIfPresent(nbtTags, "active_energy_per_tick", value -> activeEnergyPerTick = value);
        NBTUtils.setIntIfPresent(nbtTags, "status", value -> status = value);
    }

    @Override
    public void save(Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);
        nbtTags.putInt("active_recipe", activeRecipe);
        nbtTags.putInt("mode", mode);
        nbtTags.putInt("progress_ticks", progressTicks);
        nbtTags.putInt("recipe_duration", recipeDuration);
        nbtTags.putDouble("active_energy_per_tick", activeEnergyPerTick);
        nbtTags.putInt("status", status);
    }
}
