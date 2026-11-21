package meknuc.turbine.tile;

import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import meknuc.turbine.MeknucTurbineBlocks;
import meknuc.turbine.MeknucTurbineMultiblock;
import meknuc.turbine.MeknucTurbineMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 高压蒸汽涡轮的外壳方块实体（也是多方块主方块）。
 * <p>
 * 直接继承 Mekanism 的 {@link TileEntityTurbineCasing}，因此可以直接复用
 * Mekanism 工业涡轮的容器与 GUI。
 */
public class TileEntityHighPressureTurbineCasing extends TileEntityTurbineCasing {

    public TileEntityHighPressureTurbineCasing(BlockPos pos, BlockState state) {
        this(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_CASING, pos, state);
    }

    protected TileEntityHighPressureTurbineCasing(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @NotNull
    @Override
    public TurbineMultiblockData createMultiblock() {
        return new MeknucTurbineMultiblockData(this);
    }

    @NotNull
    @Override
    public MultiblockManager<TurbineMultiblockData> getManager() {
        return MeknucTurbineMultiblock.MANAGER;
    }
}
