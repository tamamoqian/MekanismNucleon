package meknuc.client.jei;

import java.util.Arrays;
import java.util.List;

import meknuc.Meknuc;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblockData;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * JEI：乏燃料冷却池的燃料棒冷却配方（枯竭的热燃料棒 → 冷却完成的燃料棒）。
 */
public class MeknucSpentFuelPoolCoolingCategory extends AbstractRecipeCategory<MeknucSpentFuelPoolCoolingCategory.CoolingRecipe> {

    public static final RecipeType<CoolingRecipe> RECIPE_TYPE = RecipeType.create(
          Meknuc.MOD_ID, "spent_fuel_pool_cooling", CoolingRecipe.class);

    private static final int INPUT_X = 10;
    private static final int INPUT_Y = 12;
    private static final int ARROW_X = 42;
    private static final int ARROW_Y = 13;
    private static final int OUTPUT_X = 76;
    private static final int OUTPUT_Y = 12;
    private static final int TEXT_X = 4;
    private static final int TEXT_Y = 34;

    public MeknucSpentFuelPoolCoolingCategory(IGuiHelper helper, ItemStack icon) {
        super(RECIPE_TYPE, MeknucPwrLang.JEI_SPENT_FUEL_POOL_COOLING.translate(),
              helper.createDrawableItemStack(icon), 150, 78);
    }

    public static List<CoolingRecipe> recipes() {
        return Arrays.stream(MeknucRodType.VALUES)
              .filter(type -> type.burnTime() > 0)
              .map(type -> new CoolingRecipe(
                    ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID, "spent_fuel_pool_cooling/" + type.id()), type))
              .toList();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolingRecipe recipe, IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
              .addItemStack(MeknucItemFuelRodBase.hotStack(recipe.type(), 1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
              .addItemStack(MeknucItemFuelRodBase.depletedStack(recipe.type(), 1));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CoolingRecipe recipe, IFocusGroup focusGroup) {
        builder.addRecipeArrowWidget().setPosition(ARROW_X, ARROW_Y);
        builder.addText(List.of(
              MeknucPwrLang.JEI_SPENT_FUEL_POOL_TIME.translate(MeknucSpentFuelPoolMultiblockData.COOLING_TICKS,
                    formatTime(MeknucSpentFuelPoolMultiblockData.COOLING_TICKS)),
              MeknucPwrLang.JEI_SPENT_FUEL_POOL_WATER.translate(MeknucSpentFuelPoolMultiblockData.WATER_PER_THREAD_PER_TICK),
              MeknucPwrLang.JEI_SPENT_FUEL_POOL_STEAM.translate(MeknucSpentFuelPoolMultiblockData.STEAM_PER_THREAD_PER_TICK),
              MeknucPwrLang.JEI_SPENT_FUEL_POOL_NOTE.translate()
        ), TEXT_X, TEXT_Y);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName(CoolingRecipe recipe) {
        return ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID, "spent_fuel_pool_cooling/" + recipe.type().id());
    }

    @Override
    public com.mojang.serialization.Codec<CoolingRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return null;
    }

    private static String formatTime(int ticks) {
        int seconds = ticks / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public record CoolingRecipe(ResourceLocation id, MeknucRodType type) {
    }
}
