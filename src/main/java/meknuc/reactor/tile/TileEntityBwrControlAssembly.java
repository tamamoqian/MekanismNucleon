package meknuc.reactor.tile;

import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityBwrControlAssembly extends TileEntityInternalMultiblock {

    public TileEntityBwrControlAssembly(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }
}
