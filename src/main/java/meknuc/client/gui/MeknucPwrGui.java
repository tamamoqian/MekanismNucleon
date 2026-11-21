package meknuc.client.gui;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.GuiBigLight;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiDynamicHorizontalRateBar;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.graph.GuiDoubleGraph;
import mekanism.common.lib.Color.ColorFunction;
import meknuc.client.gui.element.MeknucPwrTab;
import meknuc.client.gui.element.MeknucPwrTab.PwrTab;
import meknuc.reactor.MeknucPwrConstants;
import meknuc.reactor.MeknucPwrContainer;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucPwrMultiblockData;
import meknuc.reactor.tile.TileEntityPwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucPwrGui extends MeknucReactorGui<TileEntityPwrPart, MeknucPwrContainer> {

    private GuiDoubleGraph heatGraph;
    private TranslationButton activateButton;
    private TranslationButton autoStopButton;

    public MeknucPwrGui(MeknucPwrContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock().coolantTank,
              () -> List.of(multiblock().coolantTank), GaugeType.STANDARD, this, COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucPwrLang.GUI_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock().heatedCoolantTank,
              () -> List.of(multiblock().heatedCoolantTank), GaugeType.STANDARD, this, HOT_COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucPwrLang.GUI_HEATED_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiInnerScreen(this, PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, this::infoLines).spacing(0));
        addRenderableWidget(new MeknucPwrTab(this, tile, PwrTab.STAT));
        activateButton = addRenderableWidget(new TranslationButton(this, 8, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucPwrLang.GUI_ACTIVATE, (element, mouseX, mouseY) -> {
                  pressButton(MeknucPwrContainer.BUTTON_ACTIVATE);
                  return true;
              }, () -> EnumColor.DARK_GREEN));
        autoStopButton = addRenderableWidget(new TranslationButton(this, 92, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucPwrLang.GUI_AUTO_STOP, (element, mouseX, mouseY) -> {
                  pressButton(MeknucPwrContainer.BUTTON_AUTO_STOP);
                  return true;
              }, () -> multiblock().isAutoStopOnFuelExhausted() ? EnumColor.DARK_GREEN : EnumColor.DARK_GRAY));
        refreshAutoStopButton();
        addRenderableWidget(new GuiBigLight(this, 175, BUTTON_Y, () -> multiblock().isActive()));
        addRenderableWidget(new GuiDynamicHorizontalRateBar(this, heatBarHandler(), 8, TEMPERATURE_BAR_Y, imageWidth - 16,
              ColorFunction.HEAT));
        heatGraph = addRenderableWidget(new GuiDoubleGraph(this, 8, GRAPH_Y, imageWidth - 16, GRAPH_HEIGHT,
              temp -> Component.literal(String.format("%.0f ℃", temp))));
        heatGraph.setMinScale(1300.0);
    }

    private MeknucPwrMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    @Override
    protected int getControlRodInsertion() {
        return multiblock().getControlRodInsertion();
    }

    @Override
    protected int buttonInsertion() {
        return MeknucPwrContainer.BUTTON_INSERTION;
    }

    private void refreshAutoStopButton() {
        autoStopButton.setMessage(multiblock().isAutoStopOnFuelExhausted()
              ? MeknucPwrLang.GUI_AUTO_STOP.translate(MeknucPwrLang.GUI_AUTO_STOP_ON.translate())
              : MeknucPwrLang.GUI_AUTO_STOP.translate(MeknucPwrLang.GUI_AUTO_STOP_OFF.translate()));
    }

    private IBarInfoHandler heatBarHandler() {
        return new IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                return Component.literal(String.format("%.1f ℃", multiblock().getTemperature()));
            }

            @Override
            public double getLevel() {
                return Math.min(1.0, multiblock().getTemperature() / 1300.0);
            }
        };
    }

    private List<Component> infoLines() {
        MeknucPwrMultiblockData multiblock = multiblock();
        boolean active = multiblock.isActive();
        Component status;
        if (multiblock.isMeltedDown()) {
            status = MeknucPwrLang.GUI_STATUS.translate(EnumColor.DARK_RED,
                  MeknucPwrLang.GUI_STATE_MELTDOWN.translate());
        } else {
            status = MeknucPwrLang.GUI_STATUS.translate(active ? EnumColor.BRIGHT_GREEN : EnumColor.RED,
                  active ? MeknucPwrLang.GUI_STATE_ACTIVE.translate() : MeknucPwrLang.GUI_STATE_STOPPED.translate());
        }
        List<Component> lines = new ArrayList<>();
        lines.add(status);
        lines.add(MeknucPwrLang.GUI_TEMPERATURE.translate(temperatureColor(multiblock.getTemperature()),
              Component.literal(String.format("%.1f ℃", multiblock.getTemperature()))));
        lines.add(MeknucPwrLang.GUI_POWER.translate(powerColor(multiblock),
              Component.literal(String.format("%.1f MW", multiblock.getPowerLevel()))));
        lines.add(MeknucPwrLang.GUI_WATER_PRESSURE_SHORT.translate(EnumColor.WHITE,
              Component.literal(String.format("%.1f MPa", multiblock.getWaterPressure()))));
        if (multiblock.getMeltdownTimer() > 0) {
            lines.add(MeknucPwrLang.GUI_MELTDOWN_COUNTDOWN.translate(EnumColor.DARK_RED,
                  Component.literal(Integer.toString(multiblock.getMeltdownSeconds()))));
        } else {
            lines.add(MeknucPwrLang.GUI_BURN_TIME.translate(EnumColor.WHITE,
                  Component.literal(formatBurnTime(multiblock.getBurnTime()))));
        }
        return lines;
    }

    private static EnumColor powerColor(MeknucPwrMultiblockData multiblock) {
        return multiblock.isRodsFailed() ? EnumColor.DARK_RED : EnumColor.AQUA;
    }

    private static EnumColor temperatureColor(double temperature) {
        if (temperature < MeknucPwrConstants.DERATE_TEMPERATURE) {
            return EnumColor.BRIGHT_GREEN;
        }
        if (temperature < MeknucPwrConstants.ROD_FAILURE_TEMPERATURE) {
            return EnumColor.YELLOW;
        }
        if (temperature < 600.0) {
            return EnumColor.ORANGE;
        }
        if (temperature < MeknucPwrConstants.MELTDOWN_TEMPERATURE) {
            return EnumColor.RED;
        }
        return EnumColor.DARK_RED;
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        MeknucPwrMultiblockData multiblock = multiblock();
        activateButton.active = !multiblock.isActive() && !multiblock.isMeltedDown();
        int capacity = multiblock.getCacheCapacity();
        drawCacheBar(guiGraphics, FUEL_BAR_X, multiblock.getFuelCount(), multiblock.getInCoreCount(),
              multiblock.getFuelStack(), capacity, CACHE_FUEL_COLOR, MeknucPwrLang.GUI_FUEL, MeknucPwrLang.GUI_FUEL_HINT,
              MeknucPwrLang.GUI_IN_CORE, mouseX, mouseY);
        drawCacheBar(guiGraphics, WASTE_BAR_X, multiblock.getWasteCount(), 0, multiblock.getWasteStack(), capacity,
              CACHE_WASTE_COLOR, MeknucPwrLang.GUI_WASTE, null, MeknucPwrLang.GUI_IN_CORE, mouseX, mouseY);
        drawControlRodBar(guiGraphics);
        drawLabels(guiGraphics, MeknucPwrLang.GUI_TEMPERATURE_BAR, MeknucPwrLang.GUI_CONTROL_ROD,
              MeknucPwrLang.GUI_HEAT_GRAPH);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        refreshAutoStopButton();
        if (heatGraph != null) {
            heatGraph.addData(multiblock().getTemperature());
        }
    }
}
