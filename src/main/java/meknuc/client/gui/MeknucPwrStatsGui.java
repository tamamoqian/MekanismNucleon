package meknuc.client.gui;

import java.util.List;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import meknuc.client.gui.element.MeknucPwrTab;
import meknuc.client.gui.element.MeknucPwrTab.PwrTab;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucPwrMultiblockData;
import meknuc.reactor.tile.TileEntityPwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucPwrStatsGui
      extends MeknucReactorStatsGui<TileEntityPwrPart, EmptyTileContainer<TileEntityPwrPart>> {

    public MeknucPwrStatsGui(EmptyTileContainer<TileEntityPwrPart> container, Inventory inv,
          Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new MeknucPwrTab(this, tile, PwrTab.MAIN));
        addRenderableWidget(new GuiHeatTab(this, () -> List.of(MeknucPwrLang.GUI_TEMPERATURE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock().getTemperature(), TemperatureUnit.CELSIUS,
                    false)))));
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
              MekanismUtils.getTemperatureDisplay(multiblock.getTemperature(), TemperatureUnit.CELSIUS, true)), 68,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_WATER_PRESSURE.translate(
              Component.literal(String.format("%.2f MPa", multiblock.getWaterPressure()))), 78, titleTextColor());

        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL_STATISTICS.translate(), 92, headingTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL_ROD_COUNT.translate(formatCount(rods)), 104,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_SURFACE_AREA.translate(formatCount(multiblock.getSurfaceArea())),
              114, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_BURN_TIME_BATCH.translate(
              formatBurnTime(multiblock.getBurnTime()), formatBurnTime(multiblock.getBatchBurnTime())), 124,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_CONTROL_ROD_INSERTION.translate(
              formatCount(multiblock.getControlRodInsertion())), 134, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_IN_CORE_COUNT.translate(formatCount(multiblock.getInCoreCount())),
              144, titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_FUEL.translate(formatCount(multiblock.getFuelCount())), 154,
              titleTextColor());
        drawRow(guiGraphics, MeknucPwrLang.GUI_WASTE.translate(formatCount(multiblock.getWasteCount())), 164,
              titleTextColor());

        drawRow(guiGraphics, MeknucPwrLang.GUI_OPERATING_STATISTICS.translate(), 178, headingTextColor());
        drawRow(guiGraphics, operatingText(multiblock), 190, operatingColor(multiblock));
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
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

    private static String formatPower(double megawatts) {
        return String.format("%.1f MW", megawatts);
    }

    private static String formatRate(double millibuckets) {
        return String.format("%.0f mB/t", millibuckets);
    }
}
