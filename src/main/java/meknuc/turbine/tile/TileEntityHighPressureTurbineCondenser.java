package meknuc.turbine.tile;

import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import meknuc.turbine.MeknucTurbineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 高压蒸汽冷凝器：放在复合旋钮装置上方，把用过的蒸汽冷凝成水。
 * <p>
 * 与 Mekanism 的饱和冷凝器一样属于「内部多方块方块」，本身没有额外逻辑，
 * 冷凝速率由多方块数据按下述规则计算：每台 2 × 64,000 mB/t。
 */
public class TileEntityHighPressureTurbineCondenser extends TileEntityInternalMultiblock {

    public TileEntityHighPressureTurbineCondenser(BlockPos pos, BlockState state) {
        super(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_CONDENSER, pos, state);
    }
}
