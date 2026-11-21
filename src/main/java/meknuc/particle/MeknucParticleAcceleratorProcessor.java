package meknuc.particle;

import java.util.List;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.registration.impl.DeferredChemical;
import meknuc.particle.MeknucParticleAcceleratorRecipes.ChemicalInput;
import net.minecraft.world.item.ItemStack;

final class MeknucParticleAcceleratorProcessor {

    private final MeknucParticleAcceleratorMultiblockData data;

    MeknucParticleAcceleratorProcessor(MeknucParticleAcceleratorMultiblockData data) {
        this.data = data;
    }

    void tick() {
        List<MeknucParticleAcceleratorRecipes.Recipe> recipes = MeknucParticleAcceleratorRecipes.recipes();
        if (data.getProgressTicks() > 0) {
            MeknucParticleAcceleratorRecipes.Recipe recipe = recipeAt(recipes, data.getActiveRecipeIndex());
            if (recipe == null) {
                resetProgress();
                data.setActiveRecipeIndex(-1);
                data.setStatus(MeknucParticleAcceleratorConstants.STATUS_IDLE);
                return;
            }
            data.setRecipeDuration(scaledDuration(recipe));
            data.setActiveEnergyPerTick(scaledEnergyPerTick(recipe));
            if (!consumeEnergy(recipe)) {
                data.setStatus(MeknucParticleAcceleratorConstants.STATUS_NO_ENERGY);
                return;
            }
            data.setProgressTicks(data.getProgressTicks() + 1);
            if (data.getProgressTicks() >= data.getRecipeDuration()) {
                finishRecipe(recipe);
            } else {
                data.setStatus(MeknucParticleAcceleratorConstants.STATUS_RUNNING);
            }
            return;
        }

        int index = findStartableRecipe(recipes);
        if (index < 0) {
            reportBlockedRecipe(recipes);
            return;
        }

        MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(index);
        data.setActiveRecipeIndex(index);
        data.setRecipeDuration(scaledDuration(recipe));
        data.setActiveEnergyPerTick(scaledEnergyPerTick(recipe));

        if (recipe.hasItemInput()) {
            data.getTargetInputSlot().extractItem(recipe.inputCount(), Action.EXECUTE, AutomationType.INTERNAL);
        }
        for (ChemicalInput chemicalInput : recipe.chemicalInputs()) {
            IChemicalTank tank = data.tankFor(chemicalInput.chemical());
            if (tank != null) {
                tank.extract(chemicalInput.amount(), Action.EXECUTE, AutomationType.INTERNAL);
            }
        }
        if (!consumeEnergy(recipe)) {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_NO_ENERGY);
            return;
        }
        data.setProgressTicks(1);
        data.setStatus(MeknucParticleAcceleratorConstants.STATUS_RUNNING);
        if (data.getProgressTicks() >= data.getRecipeDuration()) {
            finishRecipe(recipe);
        }
        data.markChanged();
    }

    private MeknucParticleAcceleratorRecipes.Recipe recipeAt(
          List<MeknucParticleAcceleratorRecipes.Recipe> recipes, int index) {
        if (index < 0 || index >= recipes.size()) {
            return null;
        }
        return recipes.get(index);
    }

    private int findStartableRecipe(List<MeknucParticleAcceleratorRecipes.Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
            if (recipe.hasItemInput() && isStartable(recipes, i)) {
                return i;
            }
        }
        if (isStartable(recipes, data.getActiveRecipeIndex())) {
            return data.getActiveRecipeIndex();
        }
        for (int i = 0; i < recipes.size(); i++) {
            if (isStartable(recipes, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isStartable(List<MeknucParticleAcceleratorRecipes.Recipe> recipes, int index) {
        MeknucParticleAcceleratorRecipes.Recipe recipe = recipeAt(recipes, index);
        return recipe != null && recipe.mode() == data.getMode() && hasTarget(recipe) && hasChemicals(recipe)
              && canStoreOutput(recipe) && data.energyContainer.getEnergy() >= scaledEnergyPerTick(recipe);
    }

    private void reportBlockedRecipe(List<MeknucParticleAcceleratorRecipes.Recipe> recipes) {
        data.setProgressTicks(0);
        data.setRecipeDuration(0);
        data.setActiveEnergyPerTick(0.0D);
        ItemStack input = data.getTargetInputSlot().getStack();

        int candidate = -1;
        if (!input.isEmpty()) {
            for (int i = 0; i < recipes.size(); i++) {
                MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
                if (recipe.mode() == data.getMode() && recipe.hasItemInput()
                      && input.is(recipe.input().getItem())) {
                    candidate = i;
                    break;
                }
            }
        }
        if (candidate < 0) {
            for (int i = 0; i < recipes.size(); i++) {
                MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(i);
                if (recipe.mode() == data.getMode() && !recipe.hasItemInput() && hasTarget(recipe)) {
                    candidate = i;
                    break;
                }
            }
        }
        if (candidate < 0) {
            data.setActiveRecipeIndex(-1);
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_NO_RECIPE);
            return;
        }

        data.setActiveRecipeIndex(candidate);
        MeknucParticleAcceleratorRecipes.Recipe recipe = recipes.get(candidate);
        data.setRecipeDuration(scaledDuration(recipe));
        data.setActiveEnergyPerTick(scaledEnergyPerTick(recipe));
        if (!hasTarget(recipe)) {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_MISSING_ITEM);
        } else if (!hasChemicals(recipe)) {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_MISSING_CHEMICAL);
        } else if (!canStoreOutput(recipe)) {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_OUTPUT_FULL);
        } else if (data.energyContainer.getEnergy() < recipe.energyPerTick()) {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_NO_ENERGY);
        } else {
            data.setStatus(MeknucParticleAcceleratorConstants.STATUS_IDLE);
        }
    }

    private boolean hasTarget(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (!recipe.hasItemInput()) {
            return true;
        }
        ItemStack stack = data.getTargetInputSlot().getStack();
        return !stack.isEmpty() && stack.is(recipe.input().getItem()) && stack.getCount() >= recipe.inputCount();
    }

    private boolean hasChemicals(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        for (ChemicalInput chemicalInput : recipe.chemicalInputs()) {
            IChemicalTank tank = data.tankFor(chemicalInput.chemical());
            if (tank == null || tank.getStored() < chemicalInput.amount()) {
                return false;
            }
        }
        return true;
    }

    private boolean consumeEnergy(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        long energyPerTick = scaledEnergyPerTick(recipe);
        if (data.energyContainer.getEnergy() < energyPerTick) {
            return false;
        }
        return data.energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL) >= energyPerTick;
    }

    private boolean canStoreOutput(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (recipe.hasChemicalOutput()) {
            IChemicalTank tank = data.tankFor(recipe.chemicalOutput());
            return tank != null && tank.getStored() + recipe.chemicalOutputAmount() <= tank.getCapacity();
        }
        ItemStack result = recipe.output();
        ItemStack current = data.getTargetOutputSlot().getStack();
        if (current.isEmpty()) {
            return result.getCount() <= data.getTargetOutputSlot().getLimit(result);
        }
        return ItemStack.isSameItemSameComponents(current, result)
              && current.getCount() + result.getCount() <= data.getTargetOutputSlot().getLimit(result);
    }

    private void finishRecipe(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        if (recipe.hasChemicalOutput()) {
            IChemicalTank tank = data.tankFor(recipe.chemicalOutput());
            if (tank != null) {
                tank.insert(recipe.chemicalOutputStack(), Action.EXECUTE, AutomationType.INTERNAL);
            }
        } else {
            data.getTargetOutputSlot().insertItem(recipe.output().copy(), Action.EXECUTE, AutomationType.INTERNAL);
        }
        data.setProgressTicks(0);
        data.setStatus(MeknucParticleAcceleratorConstants.STATUS_RUNNING);
        data.markChanged();
    }

    void resetProgress() {
        data.setProgressTicks(0);
        data.setRecipeDuration(0);
        data.setActiveEnergyPerTick(0.0D);
    }

    private int scaledDuration(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        return data.scaledDuration(recipe);
    }

    private long scaledEnergyPerTick(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        return data.scaledEnergyPerTick(recipe);
    }
}
