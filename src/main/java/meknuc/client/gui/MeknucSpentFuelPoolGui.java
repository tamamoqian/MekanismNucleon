package meknuc.client.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.api.inventory.IInventorySlot;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolContainer;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblockData;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 乏燃料冷却池 GUI：左侧水/蒸汽储罐，中间统计信息与各类型冷却完成燃料棒数量。
 */
public class MeknucSpentFuelPoolGui
      extends GuiMekanismTile<TileEntitySpentFuelPoolCasing, MeknucSpentFuelPoolContainer> {

    public MeknucSpentFuelPoolGui(MeknucSpentFuelPoolContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.titleLabelY = 5;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiFluidGauge(
              () -> this.tile.getMultiblock().getWaterTank(),
              () -> this.tile.getMultiblock().getFluidTanks(null),
              GaugeType.STANDARD, this, 6, 13)
              .setLabel(Component.translatable("gui.meknuc.spent_fuel_pool.water")));
        addRenderableWidget(new GuiChemicalGauge(
              () -> this.tile.getMultiblock().getSteamTank(),
              () -> this.tile.getMultiblock().getChemicalTanks(null),
              GaugeType.STANDARD, this, 26, 13)
              .setLabel(Component.translatable("gui.meknuc.spent_fuel_pool.steam")));
        addRenderableWidget(new GuiInnerScreen(this, 44, 12, 112, 78, this::buildLines).padding(2));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderTitleText(guiGraphics);
        this.renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    @NotNull
    private List<Component> buildLines() {
        MeknucSpentFuelPoolMultiblockData multiblock = this.tile.getMultiblock();
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.meknuc.spent_fuel_pool.max_threads", multiblock.getMaxThreads()));
        lines.add(Component.translatable("gui.meknuc.spent_fuel_pool.running_threads", multiblock.getRunningThreads()));
        lines.add(Component.translatable("gui.meknuc.spent_fuel_pool.cooled", multiblock.getCooledCount()));
        lines.add(Component.translatable("gui.meknuc.spent_fuel_pool.steam_rate",
              multiblock.getRunningThreads() * MeknucSpentFuelPoolMultiblockData.STEAM_PER_THREAD_PER_TICK));
        lines.add(Component.empty());
        Map<MeknucRodType, Integer> counts = new EnumMap<>(MeknucRodType.class);
        // 只统计池子自己的输出缓存槽；容器槽位列表里还包含玩家背包，
        // 不能直接遍历 getMenu().slots，否则玩家身上的冷却棒会被误算成池内库存。
        for (IInventorySlot slot : multiblock.getOutputSlots()) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && MeknucSpentFuelPoolMultiblockData.isCooledRod(stack)) {
                counts.merge(MeknucItemFuelRodBase.getRodType(stack), stack.getCount(), Integer::sum);
            }
        }
        if (counts.isEmpty()) {
            lines.add(Component.translatable("gui.meknuc.spent_fuel_pool.none"));
        } else {
            for (Map.Entry<MeknucRodType, Integer> entry : counts.entrySet()) {
                Component name = MeknucItemFuelRodBase.depletedStack(entry.getKey(), 1).getHoverName();
                lines.add(Component.empty().append(name).append(": " + entry.getValue()));
            }
        }
        return lines;
    }
}
