package meknuc.reactor;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import meknuc.reactor.tile.TileEntityFbrPart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class MeknucFbrContainer extends MekanismTileContainer<TileEntityFbrPart> {

    public static final int BUTTON_INSERTION = 1000;

    public MeknucFbrContainer(int id, Inventory inv, TileEntityFbrPart tile) {
        super(MeknucFbrContainerTypes.FAST_BREEDER_REACTOR, id, inv, tile);
    }

    @Override
    protected void addSlots() {
    }

    /** 这个 GUI 不显示玩家物品栏：把背包槽位挪到面板外。 */
    @Override
    protected int getInventoryXOffset() {
        return -1000;
    }

    @Override
    protected int getInventoryYOffset() {
        return -1000;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < BUTTON_INSERTION) {
            return false;
        }
        tile.getMultiblock().setControlRodInsertion(id - BUTTON_INSERTION);
        return true;
    }
}
