package meknuc.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
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
import meknuc.client.gui.element.MeknucReactorTab;
import meknuc.client.gui.element.MeknucReactorTab.ReactorTab;
import meknuc.reactor.MeknucReactorContainer;
import meknuc.reactor.MeknucReactorLang;
import meknuc.reactor.MeknucReactorMultiblockData;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class MeknucReactorGui extends GuiMekanismTile<TileEntityPressurizedWaterReactorPart, MeknucReactorContainer> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor.png");
    private static final int GAUGE_Y = 13;
    private static final int GAUGE_HEIGHT = 60;
    private static final int BAR_WIDTH = 18;
    private static final int CONTROL_ROD_HEIGHT = 10;
    private static final int COOLANT_GAUGE_X = 8;
    private static final int FUEL_BAR_X = 30;
    private static final int PANEL_X = 52;
    private static final int PANEL_Y = 17;
    private static final int PANEL_WIDTH = 91;
    private static final int PANEL_HEIGHT = 56;
    private static final int HOT_COOLANT_GAUGE_X = 147;
    private static final int WASTE_BAR_X = 169;
    private static final int BUTTON_Y = 75;
    private static final int BUTTON_HEIGHT = 16;
    private static final int TEMPERATURE_BAR_Y = 102;
    private static final int CONTROL_ROD_BAR_Y = 124;
    private static final int GRAPH_Y = 146;
    private static final int GRAPH_HEIGHT = 16;
    private static final int CACHE_FUEL_COLOR = 0xFFD21129;
    private static final int CACHE_WASTE_COLOR = 0xFFEDB508;
    private static final int LABEL_COLOR = 0xFF3C3E3F;
    private static final int FRAME_DARK = 0xFF1A1A1B;
    private static final int FRAME_TRACK = 0xFF353637;
    private static final int FRAME_TICK = 0xFFB8BDBE;
    private static final int CONTROL_ROD_COLOR = 0xFF02A8C1;

    private GuiDoubleGraph heatGraph;
    private TranslationButton activateButton;
    private TranslationButton autoStopButton;
    private boolean draggingInsertion;
    private int pendingInsertion = -1;

    public MeknucReactorGui(MeknucReactorContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 256;
        titleLabelY = 4;
        inventoryLabelX = 16;
        inventoryLabelY = 164;
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiFluidGauge(() -> multiblock().waterTank,
              () -> List.of(multiblock().waterTank), GaugeType.STANDARD, this, COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucReactorLang.GUI_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock().steamTank,
              () -> List.of(multiblock().steamTank), GaugeType.STANDARD, this, HOT_COOLANT_GAUGE_X, GAUGE_Y)
              .setLabel(MeknucReactorLang.GUI_HEATED_COOLANT_TANK.translate()));
        addRenderableWidget(new GuiInnerScreen(this, PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, this::infoLines).spacing(0));
        addRenderableWidget(new MeknucReactorTab(this, tile, ReactorTab.STAT));
        addRenderableWidget(new GuiHeatTab(this, () -> List.of(MekanismLang.DISSIPATED_RATE.translate(
              MekanismUtils.getTemperatureDisplay(multiblock().getLastEnvironmentLoss(), TemperatureUnit.KELVIN,
                    false)))));
        activateButton = addRenderableWidget(new TranslationButton(this, 8, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucReactorLang.GUI_ACTIVATE, (element, mouseX, mouseY) -> {
                  pressButton(MeknucReactorContainer.BUTTON_ACTIVATE);
                  return true;
              }, () -> EnumColor.DARK_GREEN));
        autoStopButton = addRenderableWidget(new TranslationButton(this, 92, BUTTON_Y, 80, BUTTON_HEIGHT,
              MeknucReactorLang.GUI_AUTO_STOP, (element, mouseX, mouseY) -> {
                  pressButton(MeknucReactorContainer.BUTTON_AUTO_STOP);
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

    private MeknucReactorMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    private void refreshAutoStopButton() {
        autoStopButton.setMessage(multiblock().isAutoStopOnFuelExhausted()
              ? MeknucReactorLang.GUI_AUTO_STOP.translate(MeknucReactorLang.GUI_AUTO_STOP_ON.translate())
              : MeknucReactorLang.GUI_AUTO_STOP.translate(MeknucReactorLang.GUI_AUTO_STOP_OFF.translate()));
    }

    private void pressButton(int id) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
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
        MeknucReactorMultiblockData multiblock = multiblock();
        boolean active = multiblock.isActive();
        Component status;
        if (multiblock.isMeltedDown()) {
            status = MeknucReactorLang.GUI_STATUS.translate(EnumColor.DARK_RED,
                  MeknucReactorLang.GUI_STATE_MELTDOWN.translate());
        } else {
            status = MeknucReactorLang.GUI_STATUS.translate(active ? EnumColor.BRIGHT_GREEN : EnumColor.RED,
                  active ? MeknucReactorLang.GUI_STATE_ACTIVE.translate() : MeknucReactorLang.GUI_STATE_STOPPED.translate());
        }
        Component damage;
        if (multiblock.getMeltdownTimer() > 0) {
            damage = MeknucReactorLang.GUI_DAMAGE_MELTDOWN.translate(EnumColor.DARK_RED,
                  Component.literal(multiblock.getDamagePercent() + "%"),
                  Component.literal(Integer.toString(multiblock.getMeltdownSeconds())));
        } else {
            damage = MeknucReactorLang.GUI_DAMAGE.translate(EnumColor.WHITE,
                  Component.literal(multiblock.getDamagePercent() + "%"));
        }
        return List.of(
              status,
              MeknucReactorLang.GUI_BURN_TIME.translate(EnumColor.WHITE, multiblock.isBurnPaused()
                    ? MeknucReactorLang.GUI_BURN_STOPPED.translate()
                    : Component.literal(formatBurnTime(multiblock.getDisplayedBurnTime()))),
              MeknucReactorLang.GUI_TEMPERATURE.translate(EnumColor.RED,
                    MekanismUtils.getTemperatureDisplay(multiblock.getTemperature(), TemperatureUnit.KELVIN, true)),
              damage,
              MeknucReactorLang.GUI_COOLANT_USAGE.translate(EnumColor.WHITE,
                    Component.literal(TextUtils.format(multiblock.getLastFlow()))),
              MeknucReactorLang.GUI_STEAM_PRODUCTION.translate(EnumColor.WHITE,
                    Component.literal(TextUtils.format(multiblock.getLastFlow())))
        );
    }

    private static String fuelCountText(int count) {
        return count + "/" + MeknucReactorMultiblockData.CACHE_CAPACITY;
    }

    private static String formatBurnTime(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        MeknucReactorMultiblockData multiblock = multiblock();
        activateButton.active = !multiblock.isActive() && !multiblock.isMeltedDown();
        drawCacheBar(guiGraphics, FUEL_BAR_X, multiblock.getFuelCount(), multiblock.getInCoreCount(),
              multiblock.getFuelItemId(), CACHE_FUEL_COLOR, MeknucReactorLang.GUI_FUEL, MeknucReactorLang.GUI_FUEL_HINT,
              mouseX, mouseY);
        drawCacheBar(guiGraphics, WASTE_BAR_X, multiblock.getWasteCount(), 0, multiblock.getWasteItemId(),
              CACHE_WASTE_COLOR, MeknucReactorLang.GUI_WASTE, null, mouseX, mouseY);
        drawControlRodBar(guiGraphics);
        drawLabels(guiGraphics, multiblock);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawCacheBar(GuiGraphics guiGraphics, int x, int count, int inCore, int itemId, int color,
          MeknucReactorLang label, MeknucReactorLang hint, int mouseX, int mouseY) {
        drawBarFrame(guiGraphics, x, GAUGE_Y, BAR_WIDTH, GAUGE_HEIGHT);
        int stored = Math.min(MeknucReactorMultiblockData.CACHE_CAPACITY, count + inCore);
        int fill = (int) ((GAUGE_HEIGHT - 4) * (stored / (double) MeknucReactorMultiblockData.CACHE_CAPACITY));
        if (fill > 0) {
            guiGraphics.fill(x + 2, GAUGE_Y + GAUGE_HEIGHT - 2 - fill, x + BAR_WIDTH - 2, GAUGE_Y + GAUGE_HEIGHT - 2, color);
        }
        if (isHovering(mouseX - leftPos, mouseY - topPos, x, GAUGE_Y, BAR_WIDTH, GAUGE_HEIGHT)) {
            List<Component> lines = new ArrayList<>();
            lines.add(label.translate(EnumColor.WHITE, Component.literal(fuelCountText(count))));
            Component name = itemName(itemId);
            if (name != null) {
                lines.add(name);
            }
            if (inCore > 0) {
                lines.add(MeknucReactorLang.GUI_IN_CORE.translate(EnumColor.AQUA, Component.literal(Integer.toString(inCore))));
            }
            if (hint != null && stored == 0) {
                lines.add(hint.translate(EnumColor.GRAY));
            }
            drawTooltip(guiGraphics, mouseX, mouseY, lines);
        }
    }

    private static Component itemName(int itemId) {
        if (itemId <= 0) {
            return null;
        }
        Item item = BuiltInRegistries.ITEM.byId(itemId);
        return item == null || item == Items.AIR ? null : item.getDefaultInstance().getHoverName();
    }

    private void drawControlRodBar(GuiGraphics guiGraphics) {
        drawBarFrame(guiGraphics, 8, CONTROL_ROD_BAR_Y, imageWidth - 16, CONTROL_ROD_HEIGHT);
        int width = imageWidth - 20;
        int fill = (int) Math.round(width * Mth.clamp(displayedInsertion() / 100.0, 0.0, 1.0));
        if (fill > 0) {
            guiGraphics.fill(10, CONTROL_ROD_BAR_Y + 2, 10 + fill, CONTROL_ROD_BAR_Y + CONTROL_ROD_HEIGHT - 2, CONTROL_ROD_COLOR);
        }
        guiGraphics.fill(9 + fill, CONTROL_ROD_BAR_Y + 1, 11 + fill, CONTROL_ROD_BAR_Y + CONTROL_ROD_HEIGHT - 1, FRAME_TICK);
    }

    private int displayedInsertion() {
        int server = multiblock().getControlRodInsertion();
        if (pendingInsertion >= 0 && pendingInsertion != server) {
            return pendingInsertion;
        }
        pendingInsertion = -1;
        return server;
    }

    private boolean isOverInsertionBar(double mouseX, double mouseY) {
        return isHovering((int) mouseX - leftPos, (int) mouseY - topPos, 8, CONTROL_ROD_BAR_Y, imageWidth - 16,
              CONTROL_ROD_HEIGHT);
    }

    private void updateInsertionFromMouse(double mouseX) {
        double fraction = Mth.clamp((mouseX - leftPos - 10.0) / (imageWidth - 20.0), 0.0, 1.0);
        int percent = (int) Math.round(fraction * 100.0);
        if (percent != displayedInsertion()) {
            pendingInsertion = percent;
            pressButton(MeknucReactorContainer.BUTTON_INSERTION + percent);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverInsertionBar(mouseX, mouseY)) {
            draggingInsertion = true;
            updateInsertionFromMouse(mouseX);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingInsertion) {
            updateInsertionFromMouse(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingInsertion && button == 0) {
            draggingInsertion = false;
            updateInsertionFromMouse(mouseX);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void drawBarFrame(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, FRAME_DARK);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, FRAME_TRACK);
        if (height > 20) {
            for (int i = 1; i <= 5; i++) {
                int tickY = y + 2 + i * (height - 4) / 6;
                guiGraphics.fill(x + 2, tickY, x + width - 2, tickY + 1, FRAME_TICK);
            }
        }
    }

    private void drawLabels(GuiGraphics guiGraphics, MeknucReactorMultiblockData multiblock) {
        guiGraphics.drawString(font, MeknucReactorLang.GUI_TEMPERATURE_BAR.translate().getString(), 8, TEMPERATURE_BAR_Y - 10,
              LABEL_COLOR, false);
        guiGraphics.drawString(font, MeknucReactorLang.GUI_CONTROL_ROD.translate().getString(), 8, CONTROL_ROD_BAR_Y - 10,
              LABEL_COLOR, false);
        guiGraphics.drawString(font, MeknucReactorLang.GUI_HEAT_GRAPH.translate().getString(), 8, GRAPH_Y - 10, LABEL_COLOR, false);
        String percent = displayedInsertion() + "%";
        guiGraphics.drawString(font, percent, imageWidth - 10 - font.width(percent), CONTROL_ROD_BAR_Y + 1, 0xFFFFFFFF, false);
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

    @Override
    public void containerTick() {
        super.containerTick();
        refreshAutoStopButton();
        if (heatGraph != null) {
            heatGraph.addData(multiblock().getTemperature());
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
