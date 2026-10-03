package meknuc.client.jei;

import java.util.List;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.MeknucPwrBlocks;
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
        registration.addRecipeCategories(new MeknucPwrFuelCategory(helper, fuelRodIcon()));
        registration.addRecipeCategories(new MeknucPwrCoolantCategory(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<MeknucPwrFuelCategory.PwrFuelRecipe> recipes = MeknucPwrFuelCategory.recipes();
        if (recipes.isEmpty()) {
            meknuc.meknuc.LOGGER.warn("No meknuc:reactor_fuel data map entries were found, JEI will not show any reactor fuel recipes");
        } else {
            meknuc.meknuc.LOGGER.info("Registering {} pressurized water reactor fuel recipes with JEI", recipes.size());
            registration.addRecipes(MeknucPwrFuelCategory.RECIPE_TYPE, recipes);
        }
        registration.addRecipes(MeknucPwrCoolantCategory.RECIPE_TYPE, MeknucPwrCoolantCategory.recipes());
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
    }

    private static ItemStack fuelRodIcon() {
        return MeknucItemFuelRodBase.freshStack(MeknucRodType.URANIUM_235_MOX, 1);
    }
}
