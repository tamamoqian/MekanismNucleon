package meknuc.wastestorage.tile;

import java.util.Collections;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.wastestorage.MeknucWasteStorageMultiblock;
import meknuc.wastestorage.MeknucWasteStorageMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 耐辐射废料存储外壳：多方块主方块。
 * <p>
 * 自身不对外暴露任何库存，所有输入输出都通过端口完成；
 * 右键外壳可以在结构成型后打开存储罐 GUI。
 */
public class TileEntityWasteStorageCasing extends TileEntityMultiblock<MeknucWasteStorageMultiblockData> {

    public TileEntityWasteStorageCasing(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucWasteStorageMultiblockData createMultiblock() {
        return new MeknucWasteStorageMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucWasteStorageMultiblockData> getManager() {
        return MeknucWasteStorageMultiblock.MANAGER;
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> Collections.emptyList();
    }
}
