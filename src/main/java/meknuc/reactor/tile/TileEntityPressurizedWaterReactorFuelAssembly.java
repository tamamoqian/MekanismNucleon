package meknuc.reactor.tile;

import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPressurizedWaterReactorFuelAssembly extends TileEntityInternalMultiblock {

    public TileEntityPressurizedWaterReactorFuelAssembly(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }
}
