package meknuc.client.gui;

import java.util.Collections;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.common.util.text.TextUtils;
import meknuc.client.gui.element.MeknucBwrTab;
import meknuc.client.gui.element.MeknucBwrTab.BwrTab;
import meknuc.reactor.MeknucBwrConstants;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrMultiblockData;
import meknuc.reactor.tile.TileEntityBwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucBwrStatsGui
      extends MeknucReactorStatsGui<TileEntityBwrPart, EmptyTileContainer<TileEntityBwrPart>> {

    public MeknucBwrStatsGui(EmptyTileContainer<TileEntityBwrPart> container, Inventory inv,
          Component title) {
        super(container, inv, title);
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
        drawRow(guiGraphics, MeknucBwrLang.GUI_TEMPERATURE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock.getTemperature() - 273.15, TemperatureUnit.CELSIUS,
                    true)), 38, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_POWER.translate(
              Component.literal(String.format("%.1f%%", multiblock.getPowerPercent()))), 48, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_HEAT_CAPACITY.translate(
              TextUtils.format((long) (rods * MeknucBwrConstants.HEAT_CAPACITY_PER_ASSEMBLY))), 58,
              titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_MAX_EXCHANGE.translate(
              TextUtils.format((long) (rods * MeknucBwrConstants.STEAM_PER_ASSEMBLY * MeknucBwrConstants.HEAT_PER_MB
                    / (MeknucBwrConstants.DESIGN_TEMPERATURE - MeknucBwrConstants.SATURATION_TEMPERATURE)
                    * multiblock.getBoilEfficiency()))), 68, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_COOLANT_CAPACITY.translate(
              TextUtils.format(multiblock.coolantCapacity())), 78, titleTextColor());

        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL_STATISTICS.translate(), 92, headingTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL_ROD_COUNT.translate(formatCount(rods)), 104,
              titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_SURFACE_AREA.translate(formatCount(multiblock.getSurfaceArea())),
              114, titleTextColor());
        drawRow(guiGraphics, multiblock.isBurnPaused()
              ? MeknucBwrLang.GUI_BURN_TIME.translate(MeknucBwrLang.GUI_BURN_STOPPED.translate())
              : MeknucBwrLang.GUI_BURN_TIME_BATCH.translate(
                    formatBurnTime(multiblock.getDisplayedBurnTime()),
                    formatBurnTime(multiblock.getDisplayedBatchBurnTime())), 124, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_CONTROL_ROD_INSERTION.translate(
              formatCount(multiblock.getControlRodInsertion())), 134, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_IN_CORE_COUNT.translate(formatCount(multiblock.getInCoreCount())),
              144, titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_FUEL.translate(formatCount(multiblock.getFuelCount())), 154,
              titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_WASTE.translate(formatCount(multiblock.getWasteCount())), 164,
              titleTextColor());

        drawRow(guiGraphics, MeknucBwrLang.GUI_OPERATING_STATISTICS.translate(), 176, headingTextColor());
        drawRow(guiGraphics, coreDamageText(multiblock), 185, coreDamageColor(multiblock));
        drawRow(guiGraphics, MeknucBwrLang.GUI_VOID_FRACTION.translate(
              Component.literal(String.format("%.1f", multiblock.getLastVoidFraction() * 100.0))), 194,
              titleTextColor());
        drawRow(guiGraphics, MeknucBwrLang.GUI_STEAM_PRODUCTION.translate(
              Component.literal(TextUtils.format((long) multiblock.getLastFlow()))), 203, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
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
}
