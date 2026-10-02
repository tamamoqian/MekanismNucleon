package meknuc.client.jei;

import java.util.List;
import meknuc.items.MeknucItemBase;
import meknuc.reactor.MeknucReactorBlocks;
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
        return ResourceLocation.fromNamespaceAndPath(meknuc.meknuc.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MeknucReactorFuelCategory(helper, fuelRodIcon()));
        registration.addRecipeCategories(new MeknucReactorCoolantCategory(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<MeknucReactorFuelCategory.ReactorFuelRecipe> recipes = MeknucReactorFuelCategory.recipes();
        if (recipes.isEmpty()) {
            meknuc.meknuc.LOGGER.warn("No meknuc:reactor_fuel data map entries were found, JEI will not show any reactor fuel recipes");
        } else {
            meknuc.meknuc.LOGGER.info("Registering {} pressurized water reactor fuel recipes with JEI", recipes.size());
            registration.addRecipes(MeknucReactorFuelCategory.RECIPE_TYPE, recipes);
        }
        registration.addRecipes(MeknucReactorCoolantCategory.RECIPE_TYPE, MeknucReactorCoolantCategory.recipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(MeknucReactorFuelCategory.RECIPE_TYPE,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CASING,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_PORT);
        registration.addRecipeCatalysts(MeknucReactorCoolantCategory.RECIPE_TYPE,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CASING,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_PORT,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);
    }

    private static ItemStack fuelRodIcon() {
        return new ItemStack(MeknucItemBase.URANIUM_235_MOX_FUEL_ROD.asItem());
    }
}
