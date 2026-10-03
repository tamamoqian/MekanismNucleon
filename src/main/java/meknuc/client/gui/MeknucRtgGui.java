package meknuc.client.gui;

import java.util.List;

import meknuc.blockentities.MeknucBlockEntityRTG;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.common.GeneratorsLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeknucRtgGui extends GuiMekanismTile<MeknucBlockEntityRTG, MekanismTileContainer<MeknucBlockEntityRTG>> {

    public MeknucRtgGui(MekanismTileContainer<MeknucBlockEntityRTG> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 48, 23, 80, 40, () -> List.of(
              GeneratorsLang.OUTPUT_RATE_SHORT.translate(formatRate(tile.getProducingRate())),
              Component.translatable("gui.meknuc.fuel_rods", tile.fuelSlot.getCount())
        )));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(
              GeneratorsLang.PRODUCING_AMOUNT.translate(formatRate(tile.getProducingRate())),
              MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(MeknucBlockEntityRTG.MAX_OUTPUT))
        )));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    private static String formatRate(double rate) {
        return String.format("%.1f FE", rate);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
