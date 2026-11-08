package meknuc.client.gui;

import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import meknuc.reactor.BwrLogicAdapterMode;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrLogicPacket;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class MeknucBwrLogicAdapterGui extends GuiMekanismTile<TileEntityBwrLogicAdapter,
      EmptyTileContainer<TileEntityBwrLogicAdapter>> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor_logic_adapter.png");
    private static final int ROW_X = 8;
    private static final int ROW_WIDTH = 160;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_START_Y = 18;
    private static final int ICON_X = ROW_X + 3;
    private static final int TEXT_X = ROW_X + 22;
    private static final int STATUS_X = 8;
    private static final int STATUS_Y = 140;
    private static final int STATUS_WIDTH = 160;
    private static final int STATUS_HEIGHT = 20;
    private static final int ROW_COLOR = 0xFF353637;
    private static final int ICON_BACKGROUND = 0xFF1A1A1B;
    private static final int SLOT_BORDER = 0xFFB8BDBE;
    private static final int LIST_FRAME = 0xFF1A1A1B;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int BORDER_SELECTED = 0xFFFFFFFF;
    private static final int BORDER_HOVER = 0xFFB8BDBE;

    public MeknucBwrLogicAdapterGui(EmptyTileContainer<TileEntityBwrLogicAdapter> container,
          Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = ROW_X * 2 + ROW_WIDTH;
        imageHeight = 166;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int index = rowAt(mouseX - leftPos, mouseY - topPos);
            if (index >= 0) {
                BwrLogicAdapterMode mode = tile.getModes()[index];
                if (mode != tile.getMode()) {
                    PacketDistributor.sendToServer(new MeknucBwrLogicPacket(tile.getBlockPos(), mode.ordinal()));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        BwrLogicAdapterMode[] modes = tile.getModes();
        BwrLogicAdapterMode selected = tile.getMode();
        int hovered = rowAt(mouseX - leftPos, mouseY - topPos);
        drawListFrame(guiGraphics, modes.length);
        for (int i = 0; i < modes.length; i++) {
            BwrLogicAdapterMode mode = modes[i];
            int y = ROW_START_Y + i * ROW_HEIGHT;
            boolean isSelected = mode == selected;
            boolean isHovered = i == hovered;
            int background = isSelected ? ROW_COLOR : scale(ROW_COLOR, isHovered ? 0.80 : 0.55);
            guiGraphics.fill(ROW_X, y, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, background);
            guiGraphics.fill(ROW_X, y, ROW_X + ROW_WIDTH, y + 1, scale(background, 0.80));
            guiGraphics.fill(ROW_X, y + ROW_HEIGHT - 1, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, scale(background, 0.55));
            drawIconSlot(guiGraphics, y);
            guiGraphics.drawString(font, mode.getTextComponent().getString(), TEXT_X, y + 6, LABEL_COLOR, false);
            if (isSelected || isHovered) {
                int border = isSelected ? BORDER_SELECTED : BORDER_HOVER;
                guiGraphics.fill(ROW_X, y, ROW_X + ROW_WIDTH, y + 1, border);
                guiGraphics.fill(ROW_X, y + ROW_HEIGHT - 1, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, border);
                guiGraphics.fill(ROW_X, y, ROW_X + 1, y + ROW_HEIGHT, border);
                guiGraphics.fill(ROW_X + ROW_WIDTH - 1, y, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, border);
            }
        }
        drawStatus(guiGraphics);
        if (hovered >= 0) {
            drawLogicTooltip(guiGraphics, mouseX, mouseY, List.of(modes[hovered].getTextComponent()));
        }
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawStatus(GuiGraphics guiGraphics) {
        guiGraphics.fill(STATUS_X, STATUS_Y, STATUS_X + STATUS_WIDTH, STATUS_Y + STATUS_HEIGHT, ROW_COLOR);
        BwrLogicAdapterMode mode = tile.getMode();
        guiGraphics.drawString(font, MeknucBwrLang.GUI_LOGIC_MODE.translate(mode.getTextComponent()), STATUS_X + 4,
              STATUS_Y + 2, 0xFFFFFFFF, false);
    }

    private static EnumColor labelColor() {
        return EnumColor.WHITE;
    }

    private void drawLogicTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, List<Component> lines) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(-leftPos, -topPos, 0.0F);
        guiGraphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
        pose.popPose();
    }

    private void drawListFrame(GuiGraphics guiGraphics, int rows) {
        int bottom = ROW_START_Y + rows * ROW_HEIGHT;
        guiGraphics.fill(ROW_X - 1, ROW_START_Y - 1, ROW_X + ROW_WIDTH + 1, ROW_START_Y, LIST_FRAME);
        guiGraphics.fill(ROW_X - 1, bottom, ROW_X + ROW_WIDTH + 1, bottom + 1, LIST_FRAME);
        guiGraphics.fill(ROW_X - 1, ROW_START_Y, ROW_X, bottom, LIST_FRAME);
        guiGraphics.fill(ROW_X + ROW_WIDTH, ROW_START_Y, ROW_X + ROW_WIDTH + 1, bottom, LIST_FRAME);
    }

    private void drawIconSlot(GuiGraphics guiGraphics, int y) {
        guiGraphics.fill(ICON_X - 2, y + 1, ICON_X + 18, y + 19, SLOT_BORDER);
        guiGraphics.fill(ICON_X - 1, y + 2, ICON_X + 17, y + 18, ICON_BACKGROUND);
    }

    private static int scale(int color, double factor) {
        int red = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int green = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int blue = Math.min(255, (int) ((color & 0xFF) * factor));
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private static int rowAt(double mouseX, double mouseY) {
        if (mouseX < ROW_X || mouseX >= ROW_X + ROW_WIDTH || mouseY < ROW_START_Y) {
            return -1;
        }
        int index = (int) ((mouseY - ROW_START_Y) / ROW_HEIGHT);
        return index < BwrLogicAdapterMode.values().length ? index : -1;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
