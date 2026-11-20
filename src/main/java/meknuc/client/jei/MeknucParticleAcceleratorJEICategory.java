package meknuc.client.jei;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.recipe_viewer.jei.MekanismJEI;
import meknuc.particle.MeknucParticleAcceleratorLang;
import meknuc.particle.MeknucParticleAcceleratorRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * JEI：粒子加速器配方。
 * <p>
 * 左侧是靶材与 1~2 种等离子体，右侧是成品（物品或等离子体）；
 * 分类图标使用桌面 JEI 图标文件夹里的“粒子加速器图标”。
 */
public class MeknucParticleAcceleratorJEICategory extends AbstractRecipeCategory<MeknucParticleAcceleratorRecipes.Recipe> {

    public static final RecipeType<MeknucParticleAcceleratorRecipes.Recipe> RECIPE_TYPE = RecipeType.create(
          meknuc.meknuc.MODID, "particle_accelerator", MeknucParticleAcceleratorRecipes.Recipe.class);

    private static final int ITEM_INPUT_X = 8;
    private static final int ITEM_INPUT_Y = 8;
    private static final int CHEMICAL_INPUT_X = 8;
    private static final int CHEMICAL_INPUT_Y = 32;
    private static final int CHEMICAL_ROW = 22;
    private static final int ARROW_X = 38;
    private static final int ARROW_Y = 16;
    private static final int OUTPUT_X = 82;
    private static final int OUTPUT_Y = 14;
    private static final int TEXT_X = 4;
    private static final int TEXT_Y = 76;

    public MeknucParticleAcceleratorJEICategory(IGuiHelper helper, IDrawable icon) {
        super(RECIPE_TYPE, MeknucParticleAcceleratorLang.JEI_TITLE.translate(), icon, 150, 110);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeknucParticleAcceleratorRecipes.Recipe recipe,
          IFocusGroup focusGroup) {
        if (recipe.hasItemInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, ITEM_INPUT_X, ITEM_INPUT_Y)
                  .addItemStack(recipe.inputDisplay());
        }
        List<MeknucParticleAcceleratorRecipes.ChemicalInput> inputs = recipe.chemicalInputs();
        int chemicalInputIndex = 0;
        for (int i = 0; i < inputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, CHEMICAL_INPUT_X, CHEMICAL_INPUT_Y + chemicalInputIndex * CHEMICAL_ROW)
                  .addIngredient(MekanismJEI.TYPE_CHEMICAL, inputs.get(i).stack());
            chemicalInputIndex++;
        }
        if (recipe.hasChemicalOutput()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                  .addIngredient(MekanismJEI.TYPE_CHEMICAL, recipe.chemicalOutputStack());
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                  .addItemStack(recipe.outputDisplay());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MeknucParticleAcceleratorRecipes.Recipe recipe,
          IFocusGroup focusGroup) {
        builder.addRecipeArrowWidget().setPosition(ARROW_X, ARROW_Y);
        List<FormattedText> lines = new ArrayList<>();
        lines.add(MeknucParticleAcceleratorLang.JEI_MODE.translate(modeName(recipe.mode())));
        lines.add(MeknucParticleAcceleratorLang.JEI_TIME.translate(recipe.durationTicks(),
              formatTime(recipe.durationTicks())));
        lines.add(MeknucParticleAcceleratorLang.JEI_ENERGY.translate(formatEnergy(recipe.energyPerTick())));
        builder.addText(lines, TEXT_X, TEXT_Y);
    }

    private static String modeName(int mode) {
        return mode == MeknucParticleAcceleratorRecipes.MODE_COLLECTION
              ? MeknucParticleAcceleratorLang.GUI_MODE_COLLECTION.translate().getString()
              : MeknucParticleAcceleratorLang.GUI_MODE_ACCELERATION.translate().getString();
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName(MeknucParticleAcceleratorRecipes.Recipe recipe) {
        return recipe.id();
    }

    @Nullable
    @Override
    public Codec<MeknucParticleAcceleratorRecipes.Recipe> getCodec(ICodecHelper codecHelper,
          IRecipeManager recipeManager) {
        return null;
    }

    private static String formatTime(int ticks) {
        if (ticks < 20) {
            return ticks + "t";
        }
        int totalSeconds = ticks / 20;
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    private static String formatEnergy(long fe) {
        if (fe >= 1_000_000_000L) {
            return String.format("%.2f GFE", fe / 1_000_000_000.0D);
        }
        if (fe >= 1_000_000L) {
            return String.format("%.2f MFE", fe / 1_000_000.0D);
        }
        if (fe >= 1_000L) {
            return String.format("%.1f kFE", fe / 1_000.0D);
        }
        return fe + " FE";
    }
}
