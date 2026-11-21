package meknuc.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MeknucReactorGui<T extends TileEntityMekanism, C extends MekanismTileContainer<T>>
      extends GuiMekanismTile<T, C> {

    protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor.png");
    protected static final int GAUGE_Y = 13;
    protected static final int GAUGE_HEIGHT = 60;
    protected static final int BAR_WIDTH = 18;
    protected static final int CONTROL_ROD_HEIGHT = 10;
    protected static final int COOLANT_GAUGE_X = 8;
    protected static final int FUEL_BAR_X = 30;
    protected static final int PANEL_X = 52;
    protected static final int PANEL_Y = 17;
    protected static final int PANEL_WIDTH = 91;
    protected static final int PANEL_HEIGHT = 56;
    protected static final int HOT_COOLANT_GAUGE_X = 147;
    protected static final int WASTE_BAR_X = 169;
    protected static final int BUTTON_Y = 75;
    protected static final int BUTTON_HEIGHT = 16;
    protected static final int TEMPERATURE_BAR_Y = 102;
    protected static final int CONTROL_ROD_BAR_Y = 124;
    protected static final int GRAPH_Y = 146;
    protected static final int GRAPH_HEIGHT = 16;
    protected static final int CACHE_FUEL_COLOR = 0xFFD21129;
    protected static final int CACHE_WASTE_COLOR = 0xFFEDB508;
    protected static final int LABEL_COLOR = 0xFF3C3E3F;
    protected static final int FRAME_DARK = 0xFF1A1A1B;
    protected static final int FRAME_TRACK = 0xFF353637;
    protected static final int FRAME_TICK = 0xFFB8BDBE;
    protected static final int CONTROL_ROD_COLOR = 0xFF02A8C1;

    private boolean draggingInsertion;
    private int pendingInsertion = -1;

    protected MeknucReactorGui(C container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 256;
        titleLabelY = 4;
        inventoryLabelX = 16;
        inventoryLabelY = 164;
        dynamicSlots = true;
    }

    protected abstract int getControlRodInsertion();

    protected abstract int buttonInsertion();

    protected void pressButton(int id) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    protected static String fuelCountText(int count, int capacity) {
        return count + "/" + capacity;
    }

    protected static String formatBurnTime(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    protected void drawCacheBar(GuiGraphics guiGraphics, int x, int count, int inCore, ItemStack stack, int capacity,
          int color, ILangEntry label, ILangEntry hint, ILangEntry inCoreLabel, int mouseX, int mouseY) {
        drawBarFrame(guiGraphics, x, GAUGE_Y, BAR_WIDTH, GAUGE_HEIGHT);
        int stored = Math.min(capacity, count + inCore);
        int fill = capacity <= 0 ? 0 : (int) ((GAUGE_HEIGHT - 4) * (stored / (double) capacity));
        if (fill > 0) {
            guiGraphics.fill(x + 2, GAUGE_Y + GAUGE_HEIGHT - 2 - fill, x + BAR_WIDTH - 2, GAUGE_Y + GAUGE_HEIGHT - 2, color);
        }
        if (isHovering(mouseX - leftPos, mouseY - topPos, x, GAUGE_Y, BAR_WIDTH, GAUGE_HEIGHT)) {
            List<Component> lines = new ArrayList<>();
            lines.add(label.translate(EnumColor.WHITE, Component.literal(fuelCountText(count, capacity))));
            if (!stack.isEmpty()) {
                lines.add(stack.getHoverName());
            }
            if (inCore > 0) {
                lines.add(inCoreLabel.translate(EnumColor.AQUA, Component.literal(Integer.toString(inCore))));
            }
            if (hint != null && stored == 0) {
                lines.add(hint.translate(EnumColor.GRAY));
            }
            drawTooltip(guiGraphics, mouseX, mouseY, lines);
        }
    }

    protected void drawControlRodBar(GuiGraphics guiGraphics) {
        drawBarFrame(guiGraphics, 8, CONTROL_ROD_BAR_Y, imageWidth - 16, CONTROL_ROD_HEIGHT);
        int width = imageWidth - 20;
        int fill = (int) Math.round(width * Mth.clamp(displayedInsertion() / 100.0, 0.0, 1.0));
        if (fill > 0) {
            guiGraphics.fill(10, CONTROL_ROD_BAR_Y + 2, 10 + fill, CONTROL_ROD_BAR_Y + CONTROL_ROD_HEIGHT - 2, CONTROL_ROD_COLOR);
        }
        guiGraphics.fill(9 + fill, CONTROL_ROD_BAR_Y + 1, 11 + fill, CONTROL_ROD_BAR_Y + CONTROL_ROD_HEIGHT - 1, FRAME_TICK);
    }

    private int displayedInsertion() {
        int server = getControlRodInsertion();
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
            pressButton(buttonInsertion() + percent);
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

    protected void drawLabels(GuiGraphics guiGraphics, ILangEntry temperatureBarLabel, ILangEntry controlRodLabel,
          ILangEntry heatGraphLabel) {
        guiGraphics.drawString(font, temperatureBarLabel.translate().getString(), 8, TEMPERATURE_BAR_Y - 10, LABEL_COLOR,
              false);
        guiGraphics.drawString(font, controlRodLabel.translate().getString(), 8, CONTROL_ROD_BAR_Y - 10, LABEL_COLOR,
              false);
        guiGraphics.drawString(font, heatGraphLabel.translate().getString(), 8, GRAPH_Y - 10, LABEL_COLOR, false);
        String percent = displayedInsertion() + "%";
        guiGraphics.drawString(font, percent, imageWidth - 10 - font.width(percent), CONTROL_ROD_BAR_Y + 1, 0xFFFFFFFF,
              false);
    }

    protected static boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
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
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
