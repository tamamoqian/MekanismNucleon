package meknuc.client.gui;

import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MeknucReactorLogicAdapterGui<M, T extends TileEntityMekanism, C extends MekanismTileContainer<T>>
      extends GuiMekanismTile<T, C> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor_logic_adapter.png");
    protected static final int ROW_X = 8;
    protected static final int ROW_WIDTH = 160;
    protected static final int ROW_HEIGHT = 20;
    protected static final int ROW_START_Y = 18;
    protected static final int ICON_X = ROW_X + 3;
    protected static final int TEXT_X = ROW_X + 22;
    private static final int STATUS_X = 8;
    private static final int STATUS_Y = 140;
    private static final int STATUS_WIDTH = 160;
    private static final int STATUS_HEIGHT = 20;
    protected static final int ROW_COLOR = 0xFF353637;
    private static final int ICON_BACKGROUND = 0xFF1A1A1B;
    private static final int SLOT_BORDER = 0xFFB8BDBE;
    private static final int LIST_FRAME = 0xFF1A1A1B;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int BORDER_SELECTED = 0xFFFFFFFF;
    private static final int BORDER_HOVER = 0xFFB8BDBE;

    protected MeknucReactorLogicAdapterGui(C container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = ROW_X * 2 + ROW_WIDTH;
        imageHeight = 166;
    }

    protected abstract M[] modes();

    protected abstract M selectedMode();

    protected abstract Component modeLabel(M mode);

    protected abstract ItemStack modeIcon(M mode);

    protected abstract int modeRowColor(M mode);

    protected abstract List<Component> modeTooltip(M mode);

    protected abstract void sendMode(M mode);

    protected abstract List<Component> statusLines();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            M[] modes = modes();
            int index = rowAt(mouseX - leftPos, mouseY - topPos, modes.length);
            if (index >= 0) {
                M mode = modes[index];
                if (mode != selectedMode()) {
                    sendMode(mode);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        M[] modes = modes();
        M selected = selectedMode();
        int hovered = rowAt(mouseX - leftPos, mouseY - topPos, modes.length);
        drawListFrame(guiGraphics, modes.length);
        for (int i = 0; i < modes.length; i++) {
            int y = ROW_START_Y + i * ROW_HEIGHT;
            boolean isSelected = modes[i] == selected;
            boolean isHovered = i == hovered;
            int base = modeRowColor(modes[i]);
            int background = isSelected ? base : scale(base, isHovered ? 0.80 : 0.55);
            guiGraphics.fill(ROW_X, y, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, background);
            guiGraphics.fill(ROW_X, y, ROW_X + ROW_WIDTH, y + 1, scale(background, 0.80));
            guiGraphics.fill(ROW_X, y + ROW_HEIGHT - 1, ROW_X + ROW_WIDTH, y + ROW_HEIGHT, scale(background, 0.55));
            drawIconSlot(guiGraphics, y);
            ItemStack icon = modeIcon(modes[i]);
            if (!icon.isEmpty()) {
                renderItem(guiGraphics, icon, ICON_X, y + 2);
            }
            guiGraphics.drawString(font, modeLabel(modes[i]), TEXT_X, y + 6, LABEL_COLOR, false);
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
            drawLogicTooltip(guiGraphics, mouseX, mouseY, modeTooltip(modes[hovered]));
        }
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    private void drawStatus(GuiGraphics guiGraphics) {
        guiGraphics.fill(STATUS_X, STATUS_Y, STATUS_X + STATUS_WIDTH, STATUS_Y + STATUS_HEIGHT, ROW_COLOR);
        List<Component> lines = statusLines();
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), STATUS_X + 4, STATUS_Y + 2 + i * 9, 0xFFFFFFFF, false);
        }
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

    private static int rowAt(double mouseX, double mouseY, int rows) {
        if (mouseX < ROW_X || mouseX >= ROW_X + ROW_WIDTH || mouseY < ROW_START_Y) {
            return -1;
        }
        int index = (int) ((mouseY - ROW_START_Y) / ROW_HEIGHT);
        return index < rows ? index : -1;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
