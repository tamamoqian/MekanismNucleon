package meknuc.client.jei;

import com.mojang.serialization.Codec;
import java.util.List;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.recipe_viewer.jei.BaseRecipeCategory;
import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import meknuc.chemicals.MeknucChemicals;
import meknuc.reactor.MeknucReactorBlocks;
import meknuc.reactor.MeknucReactorLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class MeknucReactorCoolantCategory extends BaseRecipeCategory<MeknucReactorCoolantCategory.CoolantLoopRecipe> {

    public static final RecipeType<CoolantLoopRecipe> RECIPE_TYPE = RecipeType.create(
          meknuc.meknuc.MODID, "reactor_coolant", CoolantLoopRecipe.class);
    public static final IRecipeViewerRecipeType<CoolantLoopRecipe> RECIPE_VIEWER_TYPE = new CoolantLoopRecipeType();

    private static final int WATER_TANK_X = 6;
    private static final int TANK_Y = 14;
    private static final int STEAM_TANK_X = 152;
    private static final int ARROW_X = 79;
    private static final int ARROW_Y = 35;
    private static final long PROCESSED_AMOUNT = 1L;

    private final GuiGauge<?> waterTank;
    private final GuiGauge<?> steamTank;

    public MeknucReactorCoolantCategory(IGuiHelper helper) {
        super(helper, RECIPE_VIEWER_TYPE);
        this.waterTank = addElement(GuiFluidGauge.getDummy(GaugeType.STANDARD, this, WATER_TANK_X, TANK_Y)
              .setLabel(MeknucReactorLang.GUI_COOLANT_TANK.translateColored(EnumColor.INDIGO)));
        this.steamTank = addElement(GuiChemicalGauge.getDummy(GaugeType.STANDARD, this, STEAM_TANK_X, TANK_Y)
              .setLabel(MeknucReactorLang.GUI_HEATED_COOLANT_TANK.translateColored(EnumColor.ORANGE)));
    }

    public static List<CoolantLoopRecipe> recipes() {
        return List.of(new CoolantLoopRecipe(ResourceLocation.fromNamespaceAndPath(meknuc.meknuc.MODID, "reactor_coolant/reactor")));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolantLoopRecipe recipe, IFocusGroup focusGroup) {
        initFluid(builder, RecipeIngredientRole.INPUT, waterTank, List.of(water(PROCESSED_AMOUNT)));
        initChemical(builder, RecipeIngredientRole.OUTPUT, steamTank, List.of(highPressureSteam(PROCESSED_AMOUNT)));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CoolantLoopRecipe recipe, IFocusGroup focusGroup) {
        super.createRecipeExtras(builder, recipe, focusGroup);
        builder.addRecipeArrowWidget().setPosition(ARROW_X, ARROW_Y);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName(CoolantLoopRecipe recipe) {
        return recipe.id();
    }

    @Override
    public Codec<CoolantLoopRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return null;
    }

    private static ChemicalStack highPressureSteam(long amount) {
        return MeknucChemicals.HIGH_PRESSURE_STEAM.asStack(amount);
    }

    private static FluidStack water(long amount) {
        return new FluidStack(Fluids.WATER, (int) amount);
    }

    public record CoolantLoopRecipe(ResourceLocation id) {
    }

    private static final class CoolantLoopRecipeType implements IRecipeViewerRecipeType<CoolantLoopRecipe> {

        private static final int WIDTH = 176;
        private static final int HEIGHT = 100;
        private static final List<ItemLike> WORKSTATIONS = List.of(
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CASING,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_PORT,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);

        @Override
        public ResourceLocation id() {
            return RECIPE_TYPE.getUid();
        }

        @Override
        public Class<? extends CoolantLoopRecipe> recipeClass() {
            return CoolantLoopRecipe.class;
        }

        @Override
        public boolean requiresHolder() {
            return false;
        }

        @Override
        public ItemStack iconStack() {
            return new ItemStack(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CASING.asItem());
        }

        @Nullable
        @Override
        public ResourceLocation icon() {
            return null;
        }

        @Override
        public int xOffset() {
            return 0;
        }

        @Override
        public int yOffset() {
            return 0;
        }

        @Override
        public int width() {
            return WIDTH;
        }

        @Override
        public int height() {
            return HEIGHT;
        }

        @Override
        public List<ItemLike> workstations() {
            return WORKSTATIONS;
        }

        @Override
        public Component getTextComponent() {
            return MeknucReactorLang.JEI_REACTOR_COOLANT.translate();
        }
    }
}
