package meknuc.client.jei;

import com.mojang.serialization.Codec;
import java.util.List;
import meknuc.reactor.MeknucPwrFuels;
import meknuc.reactor.MeknucPwrFuels.ReactorFuel;
import meknuc.reactor.MeknucPwrLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MeknucPwrFuelCategory extends AbstractRecipeCategory<MeknucPwrFuelCategory.PwrFuelRecipe> {

    public static final RecipeType<PwrFuelRecipe> RECIPE_TYPE = RecipeType.create(
          meknuc.meknuc.MODID, "reactor_fuel", PwrFuelRecipe.class);

    private static final int INPUT_X = 10;
    private static final int INPUT_Y = 12;
    private static final int ARROW_X = 40;
    private static final int ARROW_Y = 13;
    private static final int OUTPUT_X = 74;
    private static final int OUTPUT_Y = 12;
    private static final int TEXT_X = 4;
    private static final int TEXT_Y = 34;

    public MeknucPwrFuelCategory(IGuiHelper helper, ItemStack icon) {
        super(RECIPE_TYPE, MeknucPwrLang.JEI_REACTOR_FUEL.translate(), helper.createDrawableItemStack(icon),
              128, 58);
    }

    public static List<PwrFuelRecipe> recipes() {
        return MeknucPwrFuels.fuels().entrySet().stream()
              .map(entry -> new PwrFuelRecipe(entry.getKey().location(), entry.getValue()))
              .toList();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PwrFuelRecipe recipe, IFocusGroup focusGroup) {
        Holder<Item> fuel = BuiltInRegistries.ITEM.getHolder(recipe.id()).orElse(null);
        if (fuel != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                  .addItemStack(new ItemStack(fuel.value()));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
              .addItemStack(MeknucPwrFuels.product(recipe.data(), 1));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, PwrFuelRecipe recipe, IFocusGroup focusGroup) {
        builder.addRecipeArrowWidget().setPosition(ARROW_X, ARROW_Y);
        builder.addText(List.of(
              MeknucPwrLang.JEI_REACTOR_FUEL_BURN_TIME.translate(recipe.burnTime(), formatTime(recipe.burnTime())),
              MeknucPwrLang.JEI_REACTOR_FUEL_BATCH.translate()
        ), TEXT_X, TEXT_Y);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName(PwrFuelRecipe recipe) {
        return ResourceLocation.fromNamespaceAndPath(recipe.id().getNamespace(),
              "reactor_fuel/" + recipe.id().getPath());
    }

    @Override
    public Codec<PwrFuelRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return null;
    }

    private static String formatTime(int burnTime) {
        int seconds = burnTime / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public record PwrFuelRecipe(ResourceLocation id, ReactorFuel data) {

        public int burnTime() {
            return data.burnTime();
        }
    }
}
