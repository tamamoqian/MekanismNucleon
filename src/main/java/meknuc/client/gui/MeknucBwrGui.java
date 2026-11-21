package meknuc.client.gui;

import java.util.List;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.GuiBigLight;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiDynamicHorizontalRateBar;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.graph.GuiDoubleGraph;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.common.MekanismLang;
import mekanism.common.lib.Color.ColorFunction;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.TextUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import meknuc.client.gui.element.MeknucBwrTab;
import meknuc.client.gui.element.MeknucBwrTab.BwrTab;
import meknuc.reactor.MeknucBwrContainer;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrMultiblockData;
import meknuc.reactor.tile.TileEntityBwrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucBwrGui extends MeknucReactorGui<TileEntityBwrPart, MeknucBwrContainer> {

    private GuiDoubleGraph heatGraph;
    private TranslationButton activateButton;
    private TranslationButton autoStopButton;

    public MeknucBwrGui(MeknucBwrContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiFluidGauge(() -> multiblock().waterTank,
              () -> List.of(multiblock().waterTank), GaugeType.STANDARD, this, COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucBwrLang.GUI_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock().steamTank,
              () -> List.of(multiblock().steamTank), GaugeType.STANDARD, this, HOT_COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucBwrLang.GUI_HEATED_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiInnerScreen(this, PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, this::infoLines).spacing(0));
        addRenderableWidget(new MeknucBwrTab(this, tile, BwrTab.STAT));
        addRenderableWidget(new GuiHeatTab(this, () -> List.of(MekanismLang.DISSIPATED_RATE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock().getLastEnvironmentLoss(), TemperatureUnit.KELVIN,
                    false)))));
        activateButton = addRenderableWidget(new TranslationButton(this, 8, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucBwrLang.GUI_ACTIVATE, (element, mouseX, mouseY) -> {
                  pressButton(MeknucBwrContainer.BUTTON_ACTIVATE);
                  return true;
              }, () -> EnumColor.DARK_GREEN));
        autoStopButton = addRenderableWidget(new TranslationButton(this, 92, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucBwrLang.GUI_AUTO_STOP, (element, mouseX, mouseY) -> {
                  pressButton(MeknucBwrContainer.BUTTON_AUTO_STOP);
                  return true;
              }, () -> multiblock().isAutoStopOnFuelExhausted() ? EnumColor.DARK_GREEN : EnumColor.DARK_GRAY));
        refreshAutoStopButton();
        addRenderableWidget(new GuiBigLight(this, 175, BUTTON_Y, () -> multiblock().isActive()));
        addRenderableWidget(new GuiDynamicHorizontalRateBar(this, heatBarHandler(), 8, TEMPERATURE_BAR_Y, imageWidth - 16,
              ColorFunction.HEAT));
        heatGraph = addRenderableWidget(new GuiDoubleGraph(this, 8, GRAPH_Y, imageWidth - 16, GRAPH_HEIGHT,
              temp -> MekanismUtils.getTemperatureDisplay(temp, TemperatureUnit.KELVIN, true)));
        heatGraph.setMinScale(800.0);
    }

    private MeknucBwrMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    @Override
    protected int getControlRodInsertion() {
        return multiblock().getControlRodInsertion();
    }

    @Override
    protected int buttonInsertion() {
        return MeknucBwrContainer.BUTTON_INSERTION;
    }

    private void refreshAutoStopButton() {
        autoStopButton.setMessage(multiblock().isAutoStopOnFuelExhausted()
              ? MeknucBwrLang.GUI_AUTO_STOP.translate(MeknucBwrLang.GUI_AUTO_STOP_ON.translate())
              : MeknucBwrLang.GUI_AUTO_STOP.translate(MeknucBwrLang.GUI_AUTO_STOP_OFF.translate()));
    }

    private IBarInfoHandler heatBarHandler() {
        return new IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                return MekanismUtils.getTemperatureDisplay(multiblock().getTemperature(), TemperatureUnit.KELVIN, true);
            }

            @Override
            public double getLevel() {
                return Math.min(1.0, multiblock().getTemperature() / 1100.0);
            }
        };
    }

    private List<Component> infoLines() {
        MeknucBwrMultiblockData multiblock = multiblock();
        boolean active = multiblock.isActive();
        Component status;
        if (multiblock.isMeltedDown()) {
            status = MeknucBwrLang.GUI_STATUS.translate(EnumColor.DARK_RED,
                  MeknucBwrLang.GUI_STATE_MELTDOWN.translate());
        } else {
            status = MeknucBwrLang.GUI_STATUS.translate(active ? EnumColor.BRIGHT_GREEN : EnumColor.RED,
                  active ? MeknucBwrLang.GUI_STATE_ACTIVE.translate() : MeknucBwrLang.GUI_STATE_STOPPED.translate());
        }
        Component damage;
        if (multiblock.getMeltdownTimer() > 0) {
            damage = MeknucBwrLang.GUI_DAMAGE_MELTDOWN.translate(EnumColor.DARK_RED,
                  Component.literal(multiblock.getDamagePercent() + "%"),
                  Component.literal(Integer.toString(multiblock.getMeltdownSeconds())));
        } else {
            damage = MeknucBwrLang.GUI_DAMAGE.translate(EnumColor.WHITE,
                  Component.literal(multiblock.getDamagePercent() + "%"));
        }
        return List.of(
              status,
              MeknucBwrLang.GUI_BURN_TIME.translate(EnumColor.WHITE, multiblock.isBurnPaused()
                    ? MeknucBwrLang.GUI_BURN_STOPPED.translate()
                    : Component.literal(formatBurnTime(multiblock.getDisplayedBurnTime()))),
              MeknucBwrLang.GUI_TEMPERATURE.translate(EnumColor.RED,
                    MekanismUtils.getTemperatureDisplay(multiblock.getTemperature(), TemperatureUnit.KELVIN, true)),
              damage,
              MeknucBwrLang.GUI_COOLANT_USAGE.translate(EnumColor.WHITE,
                    Component.literal(TextUtils.format(multiblock.getLastFlow()))),
              MeknucBwrLang.GUI_STEAM_PRODUCTION.translate(EnumColor.WHITE,
                    Component.literal(TextUtils.format(multiblock.getLastFlow())))
        );
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        MeknucBwrMultiblockData multiblock = multiblock();
        activateButton.active = !multiblock.isActive() && !multiblock.isMeltedDown();
        int capacity = multiblock.getCacheCapacity();
        drawCacheBar(guiGraphics, FUEL_BAR_X, multiblock.getFuelCount(), multiblock.getInCoreCount(),
              multiblock.getFuelStack(), capacity, CACHE_FUEL_COLOR, MeknucBwrLang.GUI_FUEL, MeknucBwrLang.GUI_FUEL_HINT,
              MeknucBwrLang.GUI_IN_CORE, mouseX, mouseY);
        drawCacheBar(guiGraphics, WASTE_BAR_X, multiblock.getWasteCount(), 0, multiblock.getWasteStack(), capacity,
              CACHE_WASTE_COLOR, MeknucBwrLang.GUI_WASTE, null, MeknucBwrLang.GUI_IN_CORE, mouseX, mouseY);
        drawControlRodBar(guiGraphics);
        drawLabels(guiGraphics, MeknucBwrLang.GUI_TEMPERATURE_BAR, MeknucBwrLang.GUI_CONTROL_ROD,
              MeknucBwrLang.GUI_HEAT_GRAPH);
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
