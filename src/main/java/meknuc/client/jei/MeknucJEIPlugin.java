package meknuc.client.jei;

import java.util.List;

import meknuc.Meknuc;
import meknuc.particle.MeknucParticleAcceleratorBlocks;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.MeknucPwrBlocks;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class MeknucJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MeknucPwrFuelCategory(helper, fuelRodIcon()));
        registration.addRecipeCategories(new MeknucPwrCoolantCategory(helper));
        registration.addRecipeCategories(new MeknucSpentFuelPoolCoolingCategory(helper,
              MeknucItemFuelRodBase.hotStack(MeknucRodType.URANIUM_235_MOX, 1)));
        registration.addRecipeCategories(new MeknucParticleAcceleratorJEICategory(helper,
              helper.createDrawable(ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID,
                    "textures/jei/particle_accelerator.png"), 0, 0, 16, 16)));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<MeknucPwrFuelCategory.PwrFuelRecipe> recipes = MeknucPwrFuelCategory.recipes();
        if (recipes.isEmpty()) {
            Meknuc.LOGGER.warn("No meknuc:reactor_fuel data map entries were found, JEI will not show any reactor fuel recipes");
        } else {
            Meknuc.LOGGER.info("Registering {} pressurized water reactor fuel recipes with JEI", recipes.size());
            registration.addRecipes(MeknucPwrFuelCategory.RECIPE_TYPE, recipes);
        }
        registration.addRecipes(MeknucPwrCoolantCategory.RECIPE_TYPE, MeknucPwrCoolantCategory.recipes());
        registration.addRecipes(MeknucSpentFuelPoolCoolingCategory.RECIPE_TYPE,
              MeknucSpentFuelPoolCoolingCategory.recipes());
        registration.addRecipes(MeknucParticleAcceleratorJEICategory.RECIPE_TYPE,
              meknuc.particle.MeknucParticleAcceleratorRecipes.recipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(MeknucPwrFuelCategory.RECIPE_TYPE,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CASING,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_PORT);
        registration.addRecipeCatalysts(MeknucPwrCoolantCategory.RECIPE_TYPE,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CASING,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_PORT,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);
        registration.addRecipeCatalysts(MeknucSpentFuelPoolCoolingCategory.RECIPE_TYPE,
              MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING,
              MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_PORT,
              MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_LOGIC_ADAPTER);
        registration.addRecipeCatalysts(MeknucParticleAcceleratorJEICategory.RECIPE_TYPE,
              MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_CONTROLLER,
              MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT,
              MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING);
    }

    private static ItemStack fuelRodIcon() {
        return MeknucItemFuelRodBase.freshStack(MeknucRodType.URANIUM_235_MOX, 1);
    }
}
