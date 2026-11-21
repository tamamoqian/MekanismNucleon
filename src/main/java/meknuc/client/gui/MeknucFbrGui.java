package meknuc.client.gui;

import java.util.List;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import meknuc.reactor.MeknucFbrContainer;
import meknuc.reactor.MeknucFbrMultiblockData;
import meknuc.reactor.tile.TileEntityFbrPart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * 快中子增殖堆 GUI。
 *
 * <p>除了控制棒以外的数据全部放进上方文字区，格式统一为「名称（数值）单位」；
 * 下方只保留一条带把手的控制棒插入条。</p>
 */
public class MeknucFbrGui extends GuiMekanismTile<TileEntityFbrPart, MeknucFbrContainer> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/fast_breeder_reactor.png");
    private static final ResourceLocation ENERGY_BAR = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/energy_bar_red.png");

    private static final int BAR_X = 8;
    private static final int BAR_W = 179;
    private static final int ROD_Y = 124;
    private static final int LABEL_COLOR = 0xFF262627;

    private boolean draggingRod;
    private int pendingInsertion = -1;

    public MeknucFbrGui(MeknucFbrContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 195;
        imageHeight = 145;
        titleLabelY = 4;
        dynamicSlots = false;
    }

    private MeknucFbrMultiblockData multiblock() {
        return tile.getMultiblock();
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 30, 15, 134, 91, this::infoLines).spacing(0));
    }

    private List<Component> infoLines() {
        MeknucFbrMultiblockData data = multiblock();
        return List.of(
              Component.literal("堆芯温度（" + Math.round(data.getCoreTemperature()) + "）℃"),
              Component.literal("热功率（" + Math.round(data.getPower()) + "）MW"),
              Component.literal("燃料棒（" + data.getRodsInCore() + "）根 / 耗尽（" + data.getRodsDepleted() + "）根"),
              Component.literal("氙毒（" + Math.round(data.getXenon()) + "）% / 棒效（" + Math.round(data.getRodEffectiveness() * 100.0) + "）%"),
              Component.literal("过热钠产量（" + String.format("%,d", data.getCoolantProduction()) + "）mB/t"),
              Component.literal("钠罐（" + String.format("%,d", data.getSodiumStored()) + "）mB"),
              Component.literal("过热钠罐（" + String.format("%,d", data.getHotSodiumStored()) + "）mB"),
              Component.literal("燃烧进度（" + Math.round(data.getBurnProgress()) + "）%"),
              Component.literal("增殖进度（" + (data.getBreedProgress() * 100 / 12000) + "）%"));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics g, int mouseX, int mouseY) {
        renderTitleText(g);
        g.drawString(font, "控制棒插入（" + displayedRod() + "）%", BAR_X, ROD_Y - 9, LABEL_COLOR, false);
        drawEnergyBar(g, ROD_Y, 10, displayedRod() / 100.0);
        drawRodKnob(g);
        super.drawForegroundText(g, mouseX, mouseY);
    }

    private void drawRodKnob(GuiGraphics g) {
        int kx = BAR_X + 1 + (int) Math.round((BAR_W - 2) * displayedRod() / 100.0);
        g.fill(kx - 3, ROD_Y - 2, kx + 3, ROD_Y + 12, 0xFF1A1A1B);
        g.fill(kx - 2, ROD_Y - 1, kx + 2, ROD_Y + 11, 0xFFB8BDBE);
        g.fill(kx - 1, ROD_Y, kx + 1, ROD_Y + 10, 0xFF8B8B8B);
        g.fill(kx - 3, ROD_Y + 5, kx + 3, ROD_Y + 6, 0xFF1A1A1B);
    }

    /** Mek 多方块能量条贴图（红色）横放平铺，不拉伸。 */
    private void drawEnergyBar(GuiGraphics g, int y, int height, double fill) {
        int x0 = BAR_X;
        int x1 = BAR_X + BAR_W;
        g.fill(x0, y, x1, y + height, 0xFF1A1A1B);
        g.fill(x0 + 1, y + 1, x1 - 1, y + height - 1, 0xFF2A2A2C);
        int filled = (int) Math.round((BAR_W - 2) * Mth.clamp(fill, 0.0, 1.0));
        int h = Math.min(8, height - 2);
        int drawY = y + 1 + (height - 2 - h) / 2;
        for (int px = 0; px < filled; px += 16) {
            int w = Math.min(16, filled - px);
            g.blit(ENERGY_BAR, x0 + 1 + px, drawY, 0, 0, w, h, 16, 8);
        }
    }

    private int displayedRod() {
        return pendingInsertion >= 0 ? pendingInsertion : multiblock().getControlRodInsertion();
    }

    private boolean overRodBar(double mouseX, double mouseY) {
        return mouseX >= BAR_X - 3 && mouseX < BAR_X + BAR_W + 3 && mouseY >= ROD_Y - 3 && mouseY < ROD_Y + 13;
    }

    private void updateRod(double mouseX) {
        int percent = Mth.clamp((int) Math.round((mouseX - BAR_X) / (double) BAR_W * 100.0), 0, 100);
        pendingInsertion = percent;
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, MeknucFbrContainer.BUTTON_INSERTION + percent);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && overRodBar(mouseX - leftPos, mouseY - topPos)) {
            draggingRod = true;
            updateRod(mouseX - leftPos);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingRod) {
            updateRod(mouseX - leftPos);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingRod) {
            draggingRod = false;
            pendingInsertion = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
