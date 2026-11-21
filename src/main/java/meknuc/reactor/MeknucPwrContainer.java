package meknuc.reactor;

import meknuc.reactor.tile.TileEntityPwrPart;
import net.minecraft.world.entity.player.Inventory;

public class MeknucPwrContainer extends MeknucReactorContainer<TileEntityPwrPart> {

    public MeknucPwrContainer(int id, Inventory inv, TileEntityPwrPart tile) {
        super(MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR, id, inv, tile);
    }

    @Override
    protected MeknucReactorMultiblockData multiblock() {
        return tile.getMultiblock();
    }
}
