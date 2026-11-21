package meknuc.reactor.spentpool.tile;

import java.util.Collections;
import mekanism.api.IContentsListener;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblock;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 乏燃料冷却池耐辐射外壳：多方块主方块。
 * 自身不对外暴露库存/流体能力，所有输入输出都通过端口完成。
 */
public class TileEntitySpentFuelPoolCasing extends TileEntityMultiblock<MeknucSpentFuelPoolMultiblockData> {

    public TileEntitySpentFuelPoolCasing(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucSpentFuelPoolMultiblockData createMultiblock() {
        return new MeknucSpentFuelPoolMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucSpentFuelPoolMultiblockData> getManager() {
        return MeknucSpentFuelPoolMultiblock.MANAGER;
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> Collections.emptyList();
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> Collections.emptyList();
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> Collections.emptyList();
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        return false;
    }
}
