package meknuc.blockentities;

import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MeknucBlockEntityReactorPart extends TileEntityMekanism {

    public MeknucBlockEntityReactorPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }
}
