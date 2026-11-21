package meknuc.client.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import meknuc.wastestorage.MeknucWasteStorageContainer;
import meknuc.wastestorage.MeknucWasteStorageLang;
import meknuc.wastestorage.MeknucWasteStorageMultiblockData;
import meknuc.wastestorage.MeknucWasteStorageRecipes;
import meknuc.wastestorage.tile.TileEntityWasteStorageCasing;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 放射性废料存储罐 GUI（Mekanism 风格）。
 * <p>
 * 与压水堆 / 沸水堆 / 粒子加速器一致：使用 Mekanism 标准机器面板、左侧内嵌屏幕和玩家背包，
 * 内容全部画在 {@link GuiInnerScreen} 里，不再手绘文字和进度条：
 * <ul>
 *     <li>左上：待衰变物品清单与总存量；</li>
 *     <li>右上：衰变完成缓存、并行线程、最高衰变进度与「缓存已满」提示；</li>
 *     <li>下方：五条配方各一行，显示「输入 → 产物」和该配方当前的线程进度。</li>
 * </ul>
 */
public class MeknucWasteStorageGui
      extends GuiMekanismTile<TileEntityWasteStorageCasing, MeknucWasteStorageContainer> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("meknuc",
          "gui/radiation_resistant_waste_storage.png");

    private static final int PANEL_Y = 20;
    private static final int PANEL_HEIGHT = 70;
    private static final int WASTE_PANEL_X = 8;
    private static final int WASTE_PANEL_WIDTH = 86;
    private static final int CACHE_PANEL_X = 96;
    private static final int CACHE_PANEL_WIDTH = 91;
    private static final int RECIPE_PANEL_X = 8;
    private static final int RECIPE_PANEL_Y = 96;
    private static final int RECIPE_PANEL_WIDTH = 179;
    private static final int RECIPE_PANEL_HEIGHT = 56;

    public MeknucWasteStorageGui(MeknucWasteStorageContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        // 与其它多方块机器相同的 195 × 256 面板
        imageWidth = 195;
        imageHeight = 256;
        titleLabelY = 4;
        inventoryLabelX = 16;
        inventoryLabelY = 164;
        // 让 Mekanism 统一绘制玩家背包（罐体内部的 128 个存储槽在屏幕外，不绘制）
        dynamicSlots = true;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, WASTE_PANEL_X, PANEL_Y, WASTE_PANEL_WIDTH, PANEL_HEIGHT,
              this::wasteLines).spacing(0));
        addRenderableWidget(new GuiInnerScreen(this, CACHE_PANEL_X, PANEL_Y, CACHE_PANEL_WIDTH, PANEL_HEIGHT,
              this::cacheLines).spacing(0));
        addRenderableWidget(new GuiInnerScreen(this, RECIPE_PANEL_X, RECIPE_PANEL_Y, RECIPE_PANEL_WIDTH,
              RECIPE_PANEL_HEIGHT, this::recipeLines).spacing(0));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    // ------------------------------------------------------------------ 左上：待衰变物品

    private List<Component> wasteLines() {
        MeknucWasteStorageMultiblockData data = tile.getMultiblock();
        List<Component> lines = new ArrayList<>();
        lines.add(MeknucWasteStorageLang.WASTE_HEADER.translate());
        lines.add(Component.literal(data.getStoredWasteCount() + " / " + data.getWasteCapacity()));
        List<Component> items = buildWasteLines(data);
        if (items.isEmpty()) {
            lines.add(MeknucWasteStorageLang.WASTE_EMPTY.translate());
            return lines;
        }
        int max = 4;
        for (int i = 0; i < Math.min(max, items.size()); i++) {
            lines.add(items.get(i));
        }
        if (items.size() > max) {
            lines.add(Component.literal("…"));
        }
        return lines;
    }

    /**
     * 左侧清单：先按配方顺序列出五种待衰变粉尘，再列其它放射性物品，最后是所有燃料棒合并的一行。
     */
    private List<Component> buildWasteLines(MeknucWasteStorageMultiblockData data) {
        Map<Item, Integer> counts = new LinkedHashMap<>(data.getWasteCountsByItem());
        List<Component> lines = new ArrayList<>();
        for (MeknucWasteStorageRecipes.Recipe recipe : MeknucWasteStorageRecipes.recipes()) {
            Integer count = counts.remove(recipe.input());
            if (count != null && count > 0) {
                lines.add(MeknucWasteStorageLang.WASTE_AMOUNT.translate(recipe.inputName(), count));
            }
        }
        counts.entrySet().stream()
              .sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.getKey()).toString()))
              .forEach(entry -> lines.add(MeknucWasteStorageLang.WASTE_AMOUNT.translate(
                    new ItemStack(entry.getKey()).getHoverName(), entry.getValue())));
        int rods = data.getStoredFuelRodCount();
        if (rods > 0) {
            lines.add(MeknucWasteStorageLang.FUEL_RODS.translate(rods));
        }
        return lines;
    }

    // ------------------------------------------------------------------ 右上：缓存与线程

    private List<Component> cacheLines() {
        MeknucWasteStorageMultiblockData data = tile.getMultiblock();
        List<Component> lines = new ArrayList<>();
        lines.add(MeknucWasteStorageLang.PRODUCT_HEADER.translate());
        lines.add(Component.literal(data.getStoredProductCount() + " / " + data.getProductCapacity()));
        lines.add(MeknucWasteStorageLang.THREADS.translate(
              data.getRunningThreadCount(), data.getMaxThreads()));
        lines.add(MeknucWasteStorageLang.PROGRESS.translate(leadingPercent(data) + "%"));
        if (data.isDecayPaused()) {
            lines.add(MeknucWasteStorageLang.PRODUCT_FULL.translate());
        }
        return lines;
    }

    /**
     * 五条配方里进度最高的那条，作为「当前衰变进度」显示在右上角。
     */
    private static int leadingPercent(MeknucWasteStorageMultiblockData data) {
        int percent = 0;
        for (int i = 0; i < MeknucWasteStorageRecipes.RECIPE_COUNT; i++) {
            percent = Math.max(percent, data.getDecayPercent(i));
        }
        return percent;
    }

    // ------------------------------------------------------------------ 下方：衰变配方

    private List<Component> recipeLines() {
        MeknucWasteStorageMultiblockData data = tile.getMultiblock();
        List<Component> lines = new ArrayList<>();
        lines.add(MeknucWasteStorageLang.RECIPES_HEADER.translate());
        List<MeknucWasteStorageRecipes.Recipe> recipes = MeknucWasteStorageRecipes.recipes();
        for (int i = 0; i < recipes.size(); i++) {
            MeknucWasteStorageRecipes.Recipe recipe = recipes.get(i);
            int percent = data.getDecayPercent(i);
            int threadCount = data.getThreadCount(i);
            boolean running = data.getDecayProgress(i) > 0;
            Component status;
            if (!running) {
                status = MeknucWasteStorageLang.RECIPE_IDLE.translate();
            } else if (data.isDecayPaused()) {
                status = MeknucWasteStorageLang.RECIPE_PAUSED.translate(percent + "%");
            } else if (threadCount > 1) {
                status = MeknucWasteStorageLang.RECIPE_RUNNING_THREADS.translate(percent + "%", threadCount);
            } else {
                status = MeknucWasteStorageLang.RECIPE_RUNNING.translate(percent + "%");
            }
            Component name = Component.empty().append(recipe.inputName()).append(" → ")
                  .append(recipe.outputName());
            lines.add(Component.empty().append(name).append("  ").append(status));
        }
        return lines;
    }
}
