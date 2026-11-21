package meknuc.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.render.IFancyFontRenderer.TextAlignment;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class MeknucReactorStatsGui<T extends TileEntityMekanism, C extends MekanismTileContainer<T>>
      extends GuiMekanismTile<T, C> {

    protected static final int TEXT_X = 0;
    protected static final int TEXT_PAD = 6;
    protected static final int MELTDOWN_COLOR = 0xFFFF5555;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/pressurized_water_reactor_stats.png");

    protected MeknucReactorStatsGui(C container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 216;
        titleLabelY = 6;
    }

    protected void drawRow(GuiGraphics guiGraphics, Component text, int y, int color) {
        drawScrollingString(guiGraphics, text, TEXT_X, y, TextAlignment.LEFT, color, TEXT_PAD, false);
    }

    protected static String formatCount(int value) {
        return TextUtils.format(value);
    }

    protected static String formatBurnTime(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
