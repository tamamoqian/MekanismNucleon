package meknuc.reactor;

import meknuc.reactor.tile.TileEntityBwrPart;
import net.minecraft.world.entity.player.Inventory;

public class MeknucBwrContainer extends MeknucReactorContainer<TileEntityBwrPart> {

    public MeknucBwrContainer(int id, Inventory inv, TileEntityBwrPart tile) {
        super(MeknucBwrContainerTypes.BOILING_WATER_REACTOR, id, inv, tile);
    }

    @Override
    protected MeknucReactorMultiblockData multiblock() {
        return tile.getMultiblock();
    }
}
