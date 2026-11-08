package meknuc.client.gui;

import java.util.Collections;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.client.render.IFancyFontRenderer.TextAlignment;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.common.util.text.TextUtils;
import meknuc.client.gui.element.MeknucBwrTab;
import meknuc.client.gui.element.MeknucBwrTab.BwrTab;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrMultiblockData;
import meknuc.reactor.tile.TileEntityBwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucBwrStatsGui
      extends GuiMekanismTile<TileEntityBwrPart, EmptyTileContainer<TileEntityBwrPart>> {

    private static final int TEXT_X = 0;
    private static final int TEXT_PAD = 6;
    private static final int MELTDOWN_COLOR = 0xFFFF5555;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor_stats.png");

    public MeknucBwrStatsGui(EmptyTileContainer<TileEntityBwrPart> container, Inventory inv,
          Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 216;
        titleLabelY = 6;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new MeknucBwrTab(this, tile, BwrTab.MAIN));
        addRenderableWidget(new GuiHeatTab(this, () -> Collections.singletonList(
              MekanismLang.DISSIPATED_RATE.translate(MekanismUtils.getTemperatureDisplay(
                    multiblock().getLastEnvironmentLoss(), TemperatureUnit.KELVIN, false)))));
    }

    private MeknucBwrMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        MeknucBwrMultiblockData multiblock = multiblock();
        int rods = multiblock.getFuelAssemblies();
        drawRow(guiGraphics, MeknucBwrLang.GUI_HEAT_STATISTICS.translate(), 26, headingTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_HEAT_CAPACITY.translate(
              TextUtils.format(rods * MeknucBwrMultiblockData.HEAT_CAPACITY_PER_ASSEMBLY)), 38, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_MAX_EXCHANGE.translate(
              TextUtils.format(rods * MeknucBwrMultiblockData.STEAM_PER_ASSEMBLY * MeknucBwrMultiblockData.HEAT_PER_MB
                    / (MeknucBwrMultiblockData.DESIGN_TEMPERATURE - MeknucBwrMultiblockData.SATURATION_TEMPERATURE)
                    * multiblock.getBoilEfficiency())), 48, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_COOLANT_CAPACITY.translate(
              TextUtils.format(multiblock.coolantCapacity())), 58, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_TEMPERATURE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock.getTemperature(), TemperatureUnit.KELVIN, true)), 68,
              titleTextColor());
        drawRow(guiGraphics, MekanismLang.DISSIPATED_RATE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock.getLastEnvironmentLoss(), TemperatureUnit.KELVIN, false)),
              78, titleTextColor());

        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL_STATISTICS.translate(), 92, headingTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL_ROD_COUNT.translate(formatCount(rods)), 104,
              titleTextColor());
        drawRow(guiGraphics, multiblock.isBurnPaused()
              ? MeknucBwrLang.GUI_BURN_TIME.translate(MeknucBwrLang.GUI_BURN_STOPPED.translate())
              : MeknucBwrLang.GUI_BURN_TIME_BATCH.translate(
                    formatBurnTime(multiblock.getDisplayedBurnTime()),
                    formatBurnTime(multiblock.getDisplayedBatchBurnTime())), 114, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_CONTROL_ROD_INSERTION.translate(
              formatCount(multiblock.getControlRodInsertion())), 124, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_IN_CORE_COUNT.translate(formatCount(multiblock.getInCoreCount())),
              134, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL.translate(formatCount(multiblock.getFuelCount())), 144,
              titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_WASTE.translate(formatCount(multiblock.getWasteCount())), 154,
              titleTextColor());

        drawRow(guiGraphics, MeknucBwrLang.GUI_OPERATING_STATISTICS.translate(), 168, headingTextColor());
        drawRow(guiGraphics, coreDamageText(multiblock), 180, coreDamageColor(multiblock));
        String flow = TextUtils.format(multiblock.getLastFlow());
        drawRow(guiGraphics, MeknucBwrLang.GUI_COOLANT_USAGE.translate(flow), 190, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_STEAM_PRODUCTION.translate(flow), 200, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawRow(GuiGraphics guiGraphics, Component text, int y, int color) {
        drawScrollingString(guiGraphics, text, TEXT_X, y, TextAlignment.LEFT, color, TEXT_PAD, false);
    }

    private static Component coreDamageText(MeknucBwrMultiblockData multiblock) {
        Component percent = Component.literal(multiblock.getDamagePercent() + "%");
        if (multiblock.getMeltdownTimer() > 0) {
            return MeknucBwrLang.GUI_CORE_DAMAGE_MELTDOWN.translate(percent,
                  Component.literal(Integer.toString(multiblock.getMeltdownSeconds())));
        }
        return MeknucBwrLang.GUI_CORE_DAMAGE.translate(percent);
    }

    private int coreDamageColor(MeknucBwrMultiblockData multiblock) {
        return multiblock.isMeltedDown() || multiblock.getMeltdownTimer() > 0 ? MELTDOWN_COLOR : titleTextColor();
    }

    private static String formatCount(int value) {
        return TextUtils.format(value);
    }

    private static String formatBurnTime(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
