package meknuc.reactor.tile;

import java.util.Set;
import java.util.UUID;
import mekanism.client.sound.SoundHandler;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.MeknucReactor;
import meknuc.reactor.MeknucReactorCache;
import meknuc.reactor.MeknucReactorMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPressurizedWaterReactorPart extends TileEntityMultiblock<MeknucReactorMultiblockData> {

    private boolean prevPlaying;

    public TileEntityPressurizedWaterReactorPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucReactorMultiblockData createMultiblock() {
        return new MeknucReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucReactorMultiblockData> getManager() {
        return MeknucReactor.MANAGER;
    }

    @Override
    protected boolean canPlaySound() {
        return shouldPlaySound(getMultiblock());
    }

    @Override
    protected boolean onUpdateServer(MeknucReactorMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        boolean playing = shouldPlaySound(multiblock);
        if (playing != prevPlaying) {
            prevPlaying = playing;
            needsPacket = true;
        }
        return needsPacket;
    }

    private boolean shouldPlaySound(MeknucReactorMultiblockData multiblock) {
        return isMaster() && multiblock.isFormed() && multiblock.isActive() && !multiblock.isMeltedDown()
              && multiblock.getBurnTime() > 0;
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if (!shouldPlaySound(getMultiblock())) {
            SoundHandler.stopTileSound(getSoundPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (isRemote()) {
            SoundHandler.stopTileSound(getSoundPos());
        }
    }

    @Override
    protected void structureChanged(MeknucReactorMultiblockData multiblock) {
        super.structureChanged(multiblock);
        if (!isRemote() && !multiblock.isFormed()) {
            UUID id = getCacheID();
            if (id != null) {
                MeknucReactor.MANAGER.replaceCaches(Set.of(id), id, new MeknucReactorCache());
            }
        }
    }
}
