package meknuc.client.gui;

import java.util.List;
import mekanism.api.text.EnumColor;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import meknuc.reactor.BwrLogicAdapterMode;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucReactorLogicPacket;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter.RedstoneStatus;
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
        return mode.getRenderStack();
    }

    @Override
    protected int modeRowColor(BwrLogicAdapterMode mode) {
        return 0xFF000000 | mode.getColor().getPackedColor();
    }

    @Override
    protected List<Component> modeTooltip(BwrLogicAdapterMode mode) {
        return List.of(mode.getTextComponent(), mode.getDescription());
    }

    @Override
    protected void sendMode(BwrLogicAdapterMode mode) {
        PacketDistributor.sendToServer(new MeknucReactorLogicPacket(tile.getBlockPos(), mode.ordinal()));
    }

    @Override
    protected List<Component> statusLines() {
        BwrLogicAdapterMode mode = tile.getMode();
        RedstoneStatus status = tile.getStatus();
        return List.of(
              MeknucBwrLang.GUI_LOGIC_MODE.translate(mode.getColor(), mode.getTextComponent()),
              MeknucBwrLang.GUI_STATUS.translate(statusColor(status), status.getTranslatedName()));
    }

    private static EnumColor statusColor(RedstoneStatus status) {
        return switch (status) {
            case OUTPUTTING -> EnumColor.RED;
            case POWERED -> EnumColor.BRIGHT_GREEN;
            case IDLE -> EnumColor.GRAY;
        };
    }
}
