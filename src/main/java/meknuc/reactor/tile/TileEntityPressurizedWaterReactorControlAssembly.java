package meknuc.reactor.tile;

import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPressurizedWaterReactorControlAssembly extends TileEntityInternalMultiblock {

    public TileEntityPressurizedWaterReactorControlAssembly(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }
}
