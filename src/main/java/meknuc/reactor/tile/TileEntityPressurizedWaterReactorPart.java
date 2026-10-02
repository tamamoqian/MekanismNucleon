package meknuc.reactor.tile;

import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.MeknucReactor;
import meknuc.reactor.MeknucReactorMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPressurizedWaterReactorPart extends TileEntityMultiblock<MeknucReactorMultiblockData> {

    public TileEntityPressurizedWaterReactorPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucReactorMultiblockData createMultiblock() {
        return new MeknucReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucReactorMultiblockData> getManager() {
        return MeknucReactor.MANAGER;
    }
}
