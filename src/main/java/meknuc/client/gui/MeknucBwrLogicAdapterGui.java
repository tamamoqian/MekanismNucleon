package meknuc.client.gui;

import java.util.List;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import meknuc.reactor.BwrLogicAdapterMode;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucReactorLogicPacket;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class MeknucBwrLogicAdapterGui extends MeknucReactorLogicAdapterGui<BwrLogicAdapterMode,
      TileEntityBwrLogicAdapter, EmptyTileContainer<TileEntityBwrLogicAdapter>> {

    public MeknucBwrLogicAdapterGui(EmptyTileContainer<TileEntityBwrLogicAdapter> container,
          Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected BwrLogicAdapterMode[] modes() {
        return tile.getModes();
    }

    @Override
    protected BwrLogicAdapterMode selectedMode() {
        return tile.getMode();
    }

    @Override
    protected Component modeLabel(BwrLogicAdapterMode mode) {
        return mode.getTextComponent();
    }

    @Override
    protected ItemStack modeIcon(BwrLogicAdapterMode mode) {
        return ItemStack.EMPTY;
    }

    @Override
    protected int modeRowColor(BwrLogicAdapterMode mode) {
        return ROW_COLOR;
    }

    @Override
    protected List<Component> modeTooltip(BwrLogicAdapterMode mode) {
        return List.of(mode.getTextComponent());
    }

    @Override
    protected void sendMode(BwrLogicAdapterMode mode) {
        PacketDistributor.sendToServer(new MeknucReactorLogicPacket(tile.getBlockPos(), mode.ordinal()));
    }

    @Override
    protected List<Component> statusLines() {
        return List.of(MeknucBwrLang.GUI_LOGIC_MODE.translate(tile.getMode().getTextComponent()));
    }
}
