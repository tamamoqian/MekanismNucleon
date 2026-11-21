package meknuc.client.gui;

import java.util.List;
import mekanism.api.text.EnumColor;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucReactorLogicPacket;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter.PressurizedWaterReactorLogic;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter.RedstoneStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class MeknucPwrLogicAdapterGui extends MeknucReactorLogicAdapterGui<PressurizedWaterReactorLogic,
      TileEntityPwrLogicAdapter, EmptyTileContainer<TileEntityPwrLogicAdapter>> {

    public MeknucPwrLogicAdapterGui(EmptyTileContainer<TileEntityPwrLogicAdapter> container,
          Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected PressurizedWaterReactorLogic[] modes() {
        return tile.getModes();
    }

    @Override
    protected PressurizedWaterReactorLogic selectedMode() {
        return tile.getMode();
    }

    @Override
    protected Component modeLabel(PressurizedWaterReactorLogic mode) {
        return mode.getTranslatedName();
    }

    @Override
    protected ItemStack modeIcon(PressurizedWaterReactorLogic mode) {
        return mode.getRenderStack();
    }

    @Override
    protected int modeRowColor(PressurizedWaterReactorLogic mode) {
        return 0xFF000000 | mode.getColor().getPackedColor();
    }

    @Override
    protected List<Component> modeTooltip(PressurizedWaterReactorLogic mode) {
        return List.of(mode.getTranslatedName(), mode.getDescription());
    }

    @Override
    protected void sendMode(PressurizedWaterReactorLogic mode) {
        PacketDistributor.sendToServer(new MeknucReactorLogicPacket(tile.getBlockPos(), mode.ordinal()));
    }

    @Override
    protected List<Component> statusLines() {
        PressurizedWaterReactorLogic mode = tile.getMode();
        RedstoneStatus status = tile.getStatus();
        return List.of(
              MeknucPwrLang.GUI_LOGIC_MODE.translate(mode.getColor(), mode.getTranslatedName()),
              MeknucPwrLang.GUI_STATUS.translate(statusColor(status), status.getTranslatedName()));
    }

    private static EnumColor statusColor(RedstoneStatus status) {
        return switch (status) {
            case OUTPUTTING -> EnumColor.RED;
            case POWERED -> EnumColor.BRIGHT_GREEN;
            case IDLE -> EnumColor.GRAY;
        };
    }
}
