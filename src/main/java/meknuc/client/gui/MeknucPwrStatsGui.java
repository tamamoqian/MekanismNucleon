package meknuc.client.gui;

import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.render.IFancyFontRenderer.TextAlignment;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.text.TextUtils;
import meknuc.client.gui.element.MeknucPwrTab;
import meknuc.client.gui.element.MeknucPwrTab.PwrTab;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucPwrMultiblockData;
import meknuc.reactor.tile.TileEntityPwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucPwrStatsGui
      extends GuiMekanismTile<TileEntityPwrPart, EmptyTileContainer<TileEntityPwrPart>> {

    private static final int TEXT_X = 0;
    private static final int TEXT_PAD = 6;
    private static final int MELTDOWN_COLOR = 0xFFFF5555;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor_stats.png");

    public MeknucPwrStatsGui(EmptyTileContainer<TileEntityPwrPart> container, Inventory inv,
          Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 216;
        titleLabelY = 6;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new MeknucPwrTab(this, tile, PwrTab.MAIN));
    }

    private MeknucPwrMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        MeknucPwrMultiblockData multiblock = multiblock();
        int rods = multiblock.getFuelAssemblies();
        drawRow(guiGraphics, MeknucPwrLang.GUI_HEAT_STATISTICS.translate(), 26, headingTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_POWER.translate(
              Component.literal(formatPower(multiblock.getPowerLevel()))), 38, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_REMOVAL_CAP.translate(
              Component.literal(formatPower(multiblock.getRemovalCap()))), 48, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_COOLANT_FLOW.translate(
              Component.literal(formatRate(multiblock.getBaseFlow()))), 58, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_TEMPERATURE.translate(
              Component.literal(String.format("%.1f ℃", multiblock.getTemperature()))), 68, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_WATER_PRESSURE.translate(
              Component.literal(String.format("%.2f MPa", multiblock.getWaterPressure()))), 78, titleTextColor());

        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL_STATISTICS.translate(), 92, headingTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL_ROD_COUNT.translate(formatCount(rods)), 104,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_BURN_TIME_BATCH.translate(
              formatBurnTime(multiblock.getBurnTime()), formatBurnTime(multiblock.getBatchBurnTime())), 114,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_CONTROL_ROD_INSERTION.translate(
              formatCount(multiblock.getControlRodInsertion())), 124, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_IN_CORE_COUNT.translate(formatCount(multiblock.getInCoreCount())),
              134, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL.translate(formatCount(multiblock.getFuelCount())), 144,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_WASTE.translate(formatCount(multiblock.getWasteCount())), 154,
              titleTextColor());

        drawRow(guiGraphics, MeknucPwrLang.GUI_OPERATING_STATISTICS.translate(), 168, headingTextColor());
        drawRow(guiGraphics, operatingText(multiblock), 180, operatingColor(multiblock));
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawRow(GuiGraphics guiGraphics, Component text, int y, int color) {
        drawScrollingString(guiGraphics, text, TEXT_X, y, TextAlignment.LEFT, color, TEXT_PAD, false);
    }

    private static Component operatingText(MeknucPwrMultiblockData multiblock) {
        if (multiblock.getMeltdownTimer() > 0) {
            return MeknucPwrLang.GUI_MELTDOWN_COUNTDOWN.translate(
                  Component.literal(Integer.toString(multiblock.getMeltdownSeconds())));
        }
        return MeknucPwrLang.GUI_COOLANT_RATE.translate(
              Component.literal(formatRate(multiblock.getCoolantRate())),
              Component.literal(formatRate(multiblock.getCoolantRate())));
    }

    private static int operatingColor(MeknucPwrMultiblockData multiblock) {
        if (multiblock.isMeltedDown() || multiblock.getMeltdownTimer() > 0) {
            return MELTDOWN_COLOR;
        }
        return 0xFF404040;
    }

    private static String formatCount(int value) {
        return TextUtils.format(value);
    }

    private static String formatPower(double megawatts) {
        return String.format("%.1f MW", megawatts);
    }

    private static String formatRate(double millibuckets) {
        return String.format("%.0f mB/t", millibuckets);
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
