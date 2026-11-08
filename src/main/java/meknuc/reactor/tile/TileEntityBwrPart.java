package meknuc.reactor.tile;

import java.util.Set;
import java.util.UUID;
import mekanism.client.sound.SoundHandler;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.MeknucBwrCache;
import meknuc.reactor.MeknucBwrMultiblock;
import meknuc.reactor.MeknucBwrMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityBwrPart extends TileEntityMultiblock<MeknucBwrMultiblockData> {

    public TileEntityBwrPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucBwrMultiblockData createMultiblock() {
        return new MeknucBwrMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucBwrMultiblockData> getManager() {
        return MeknucBwrMultiblock.MANAGER;
    }

    @Override
    protected boolean canPlaySound() {
        return false;
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        SoundHandler.stopTileSound(getSoundPos());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (isRemote()) {
            SoundHandler.stopTileSound(getSoundPos());
        }
    }

    @Override
    protected void structureChanged(MeknucBwrMultiblockData multiblock) {
        super.structureChanged(multiblock);
        if (!isRemote() && !multiblock.isFormed()) {
            UUID id = getCacheID();
            if (id != null) {
                MeknucBwrMultiblock.MANAGER.replaceCaches(Set.of(id), id, new MeknucBwrCache());
            }
        }
    }
}
