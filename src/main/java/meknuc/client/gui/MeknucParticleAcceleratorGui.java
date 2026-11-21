package meknuc.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiDynamicHorizontalRateBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.common.lib.Color;
import mekanism.common.lib.Color.ColorFunction;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registration.impl.DeferredChemical;
import meknuc.chemicals.MeknucChemicals;
import meknuc.particle.MeknucParticleAcceleratorConstants;
import meknuc.particle.MeknucParticleAcceleratorContainer;
import meknuc.particle.MeknucParticleAcceleratorLang;
import meknuc.particle.MeknucParticleAcceleratorMultiblockData;
import meknuc.particle.MeknucParticleAcceleratorRecipes;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * 粒子加速器 GUI（Mekanism 风格）。
 * <p>
 * 使用 Mekanism 的标准机器底图、内嵌屏幕、能量条、进度条、槽位与按钮：
 * <ul>
 *   <li>左侧：Mekanism 竖向能量条（16 GFE 起，随能量升级提升）；</li>
 *   <li>中上：内嵌屏幕显示运行状态；</li>
 *   <li>中间：内嵌屏幕显示自动检测到的配方与实时能耗，下面是 Mekanism 进度条；</li>
 *   <li>右侧：粒子束缓存（氢气 / 热质子等离子体 / 热中子等离子体）；</li>
 *   <li>底部：「粒子加速」「束流收集」两个模式按钮。</li>
 * </ul>
 * 配方不再手动选择：当前模式里只要有能跑的配方，机器就会自动运行。
 */
public class MeknucParticleAcceleratorGui extends GuiMekanismTile<TileEntityParticleAcceleratorController,
      MeknucParticleAcceleratorContainer> {

    private static final ResourceLocation GUI_ICON = ResourceLocation.fromNamespaceAndPath("meknuc",
          "textures/jei/particle_accelerator.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 20;
    private static final int ENERGY_HEIGHT = 84;

    private static final int STATUS_X = 30;
    private static final int STATUS_Y = 16;
    private static final int STATUS_WIDTH = 132;
    private static final int STATUS_HEIGHT = 18;

    private static final int INFO_X = 30;
    private static final int INFO_Y = 36;
    private static final int INFO_WIDTH = 110;
    private static final int INFO_HEIGHT = 46;

    private static final int PROGRESS_X = 30;
    private static final int PROGRESS_Y = 84;
    private static final int PROGRESS_WIDTH = 110;

    private static final int CHEMICAL_X = 142;
    private static final int CHEMICAL_Y = 30;
    private static final int CHEMICAL_ROW_HEIGHT = 22;

    private static final int INPUT_SLOT_X = 146;
    private static final int INPUT_SLOT_Y = 112;
    private static final int OUTPUT_SLOT_X = 168;
    private static final int OUTPUT_SLOT_Y = 112;

    private static final int MODE_BUTTON_Y = 146;
    private static final int MODE_BUTTON_WIDTH = 86;
    private static final int MODE_BUTTON_HEIGHT = 16;
    private static final int MODE_ACCELERATION_X = 8;
    private static final int MODE_COLLECTION_X = 99;

    private static final int COLOR_TEXT = 0xFF3C3E3F;
    private static final int COLOR_TEXT_GRAY = 0xFF6A6D6E;

    /** GUI 右侧固定按 氢气、热质子等离子体、热中子等离子体 的顺序显示。 */
    private static final List<DeferredChemical<Chemical>> DISPLAY_CHEMICALS = List.of(
          MekanismChemicals.HYDROGEN, MeknucChemicals.HOT_PROTON_PLASMA, MeknucChemicals.HOT_NEUTRON_PLASMA);
    private static final ColorFunction PROGRESS_COLOR = level -> Color.rgbai(46, 160, 92, 255);

    public MeknucParticleAcceleratorGui(MeknucParticleAcceleratorContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 256;
        titleLabelY = 4;
        inventoryLabelX = 16;
        inventoryLabelY = 164;
        // 让 Mekanism 统一绘制玩家背包与靶材槽的槽位边框
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiVerticalPowerBar(this, energyBarHandler(), ENERGY_X, ENERGY_Y, ENERGY_HEIGHT));
        addRenderableWidget(new GuiInnerScreen(this, STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT,
              this::statusLines).spacing(0));
        addRenderableWidget(new GuiInnerScreen(this, INFO_X, INFO_Y, INFO_WIDTH, INFO_HEIGHT,
              this::infoLines).spacing(0));
        addRenderableWidget(new GuiDynamicHorizontalRateBar(this, progressBarHandler(),
              PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH, PROGRESS_COLOR));
        addRenderableWidget(new TranslationButton(this, MODE_ACCELERATION_X, MODE_BUTTON_Y, MODE_BUTTON_WIDTH,
              MODE_BUTTON_HEIGHT, MeknucParticleAcceleratorLang.GUI_MODE_ACCELERATION, (element, mouseX, mouseY) -> {
                  pressMode(MeknucParticleAcceleratorRecipes.MODE_ACCELERATION);
                  return true;
              }, () -> multiblock().getMode() == MeknucParticleAcceleratorRecipes.MODE_ACCELERATION
                    ? EnumColor.BRIGHT_GREEN : EnumColor.DARK_GRAY));
        addRenderableWidget(new TranslationButton(this, MODE_COLLECTION_X, MODE_BUTTON_Y, MODE_BUTTON_WIDTH,
              MODE_BUTTON_HEIGHT, MeknucParticleAcceleratorLang.GUI_MODE_COLLECTION, (element, mouseX, mouseY) -> {
                  pressMode(MeknucParticleAcceleratorRecipes.MODE_COLLECTION);
                  return true;
              }, () -> multiblock().getMode() == MeknucParticleAcceleratorRecipes.MODE_COLLECTION
                    ? EnumColor.BRIGHT_GREEN : EnumColor.DARK_GRAY));
    }

    private MeknucParticleAcceleratorMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    private void pressMode(int mode) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
                  MeknucParticleAcceleratorContainer.BUTTON_MODE_BASE + mode);
        }
    }

    private IBarInfoHandler energyBarHandler() {
        return new IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                double stored = multiblock().getStoredEnergyDisplay();
                return Component.literal(MeknucParticleAcceleratorLang.GUI_ENERGY.translate().getString()
                      + ": " + formatEnergy(stored) + " / "
                      + formatEnergy(multiblock().getEnergyCapacity()));
            }

            @Override
            public double getLevel() {
                return Math.min(1.0D, multiblock().getStoredEnergyDisplay()
                      / Math.max(1.0D, multiblock().getEnergyCapacity()));
            }
        };
    }

    private IBarInfoHandler progressBarHandler() {
        return new IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                return Component.literal(MeknucParticleAcceleratorLang.GUI_PROGRESS.translate().getString() + ": "
                      + progressPercent(multiblock()) + "%");
            }

            @Override
            public double getLevel() {
                return progressPercent(multiblock()) / 100.0D;
            }
        };
    }

    private static int progressPercent(MeknucParticleAcceleratorMultiblockData multiblock) {
        if (multiblock.getRecipeDuration() <= 0) {
            return 0;
        }
        return Math.min(100, multiblock.getProgressTicks() * 100 / multiblock.getRecipeDuration());
    }

    private List<Component> statusLines() {
        return List.of(statusText(multiblock()));
    }

    private List<Component> infoLines() {
        MeknucParticleAcceleratorMultiblockData multiblock = multiblock();
        MeknucParticleAcceleratorRecipes.Recipe recipe = multiblock.getActiveRecipe();
        String recipeLine = recipe == null
              ? MeknucParticleAcceleratorLang.GUI_STATE_NO_RECIPE.translate().getString()
              : recipe.inputShort() + " → " + recipe.outputShort();
        // 服务端写入的实时功率已经包含速度 / 能量升级的缩放
        double rate = multiblock.getActiveEnergyPerTick();
        return List.of(
              Component.literal(recipeLine),
              MeknucParticleAcceleratorLang.GUI_ENERGY_RATE.translate(EnumColor.AQUA,
                    Component.literal(formatEnergy(rate))),
              MeknucParticleAcceleratorLang.GUI_PROGRESS.translate(EnumColor.DARK_GREEN,
                    Component.literal(Integer.toString(progressPercent(multiblock))))
        );
    }

    private Component statusText(MeknucParticleAcceleratorMultiblockData multiblock) {
        MeknucParticleAcceleratorRecipes.Recipe recipe = multiblock.getActiveRecipe();
        return switch (multiblock.getStatus()) {
            case MeknucParticleAcceleratorConstants.STATUS_RUNNING ->
                  MeknucParticleAcceleratorLang.GUI_STATE_RUNNING.translate(EnumColor.BRIGHT_GREEN);
            case MeknucParticleAcceleratorConstants.STATUS_MISSING_ITEM -> recipe == null
                  ? MeknucParticleAcceleratorLang.GUI_STATE_NO_RECIPE.translate(EnumColor.GRAY)
                  : MeknucParticleAcceleratorLang.GUI_STATE_MISSING_ITEM.translate(EnumColor.RED,
                        recipe.inputName(), recipe.inputCount());
            case MeknucParticleAcceleratorConstants.STATUS_MISSING_CHEMICAL -> recipe == null
                  ? MeknucParticleAcceleratorLang.GUI_STATE_NO_RECIPE.translate(EnumColor.GRAY)
                  : MeknucParticleAcceleratorLang.GUI_STATE_MISSING_CHEMICAL.translate(EnumColor.RED,
                        recipe.chemicalInputs().isEmpty() ? "-" : recipe.chemicalInputs().get(0).name(),
                        recipe.chemicalInputs().isEmpty() ? 0L : recipe.chemicalInputs().get(0).amount());
            case MeknucParticleAcceleratorConstants.STATUS_OUTPUT_FULL ->
                  MeknucParticleAcceleratorLang.GUI_STATE_OUTPUT_FULL.translate(EnumColor.RED);
            case MeknucParticleAcceleratorConstants.STATUS_NO_ENERGY -> recipe == null
                  ? MeknucParticleAcceleratorLang.GUI_STATE_NO_RECIPE.translate(EnumColor.GRAY)
                  : MeknucParticleAcceleratorLang.GUI_STATE_NO_ENERGY.translate(EnumColor.RED,
                        formatEnergy(multiblock.getActiveEnergyPerTick()));
            case MeknucParticleAcceleratorConstants.STATUS_NO_RECIPE ->
                  MeknucParticleAcceleratorLang.GUI_STATE_NO_RECIPE.translate(EnumColor.GRAY);
            default -> MeknucParticleAcceleratorLang.GUI_STATE_IDLE.translate(EnumColor.GRAY);
        };
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        guiGraphics.blit(GUI_ICON, imageWidth - 22, 2, 0, 0, 16, 16, 16, 16);

        MeknucParticleAcceleratorMultiblockData multiblock = multiblock();
        drawChemicals(guiGraphics, multiblock, mouseX, mouseY);
        drawTargetLabels(guiGraphics);
        drawTargetSlotTooltip(guiGraphics, mouseX, mouseY);

        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawChemicals(GuiGraphics guiGraphics, MeknucParticleAcceleratorMultiblockData multiblock,
          int mouseX, int mouseY) {
        List<IChemicalTank> tanks = multiblock.getDisplayTanks();
        for (int i = 0; i < tanks.size() && i < DISPLAY_CHEMICALS.size(); i++) {
            IChemicalTank tank = tanks.get(i);
            int y = CHEMICAL_Y + i * CHEMICAL_ROW_HEIGHT;
            String name = DISPLAY_CHEMICALS.get(i).get().getTextComponent().getString();
            String amount = tank.getStored() + " mB";
            guiGraphics.drawString(font, name, CHEMICAL_X, y, COLOR_TEXT, false);
            guiGraphics.drawString(font, amount, CHEMICAL_X, y + 10, COLOR_TEXT_GRAY, false);
            if (isHovering(mouseX - leftPos, mouseY - topPos, CHEMICAL_X, y, 56, 20)) {
                drawTooltip(guiGraphics, mouseX, mouseY, List.of(Component.literal(
                      name + ": " + tank.getStored() + " / " + tank.getCapacity() + " mB")));
            }
        }
    }

    private void drawTargetLabels(GuiGraphics guiGraphics) {
        String label = MeknucParticleAcceleratorLang.GUI_TARGET_INPUT.translate().getString();
        guiGraphics.drawString(font, label, INPUT_SLOT_X - 2, INPUT_SLOT_Y - 12, COLOR_TEXT, false);
    }

    private void drawTargetSlotTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - leftPos;
        int localY = mouseY - topPos;
        if (isHovering(localX, localY, INPUT_SLOT_X, INPUT_SLOT_Y, 16, 16)) {
            drawTooltip(guiGraphics, mouseX, mouseY,
                  List.of(MeknucParticleAcceleratorLang.GUI_TARGET_INPUT.translate()));
        } else if (isHovering(localX, localY, OUTPUT_SLOT_X, OUTPUT_SLOT_Y, 16, 16)) {
            drawTooltip(guiGraphics, mouseX, mouseY,
                  List.of(MeknucParticleAcceleratorLang.GUI_TARGET_OUTPUT.translate()));
        }
    }

    private static String formatEnergy(double fe) {
        if (fe >= 1_000_000_000.0D) {
            return String.format("%.2f GFE", fe / 1_000_000_000.0D);
        }
        if (fe >= 1_000_000.0D) {
            return String.format("%.2f MFE", fe / 1_000_000.0D);
        }
        if (fe >= 1_000.0D) {
            return String.format("%.1f kFE", fe / 1_000.0D);
        }
        return String.format("%.0f FE", fe);
    }

    private static boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, List<Component> lines) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(-leftPos, -topPos, 0.0F);
        guiGraphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
        pose.popPose();
    }
}
