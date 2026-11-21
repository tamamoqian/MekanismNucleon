package meknuc.reactor.tile;

import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.MeknucFbrMultiblock;
import meknuc.reactor.MeknucFbrMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 快中子增殖堆的普通部件（外壳、玻璃等）。
 *
 * <p>这里刻意不暴露任何容器能力：只有端口才按模式对接管道/物流，
 * 否则物流管道、化学品管道会连到外壳上。</p>
 */
public class TileEntityFbrPart extends TileEntityMultiblock<MeknucFbrMultiblockData> {

    public TileEntityFbrPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucFbrMultiblockData createMultiblock() {
        return new MeknucFbrMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucFbrMultiblockData> getManager() {
        return MeknucFbrMultiblock.MANAGER;
    }

    @Override
    protected boolean onUpdateServer(MeknucFbrMultiblockData multiblock) {
        return super.onUpdateServer(multiblock);
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> List.of();
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> List.of();
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> List.of();
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        return false;
    }
}
