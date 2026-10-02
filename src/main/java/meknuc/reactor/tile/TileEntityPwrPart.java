package meknuc.reactor.tile;

import java.util.Set;
import java.util.UUID;
import mekanism.client.sound.SoundHandler;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.reactor.MeknucPwrMultiblock;
import meknuc.reactor.MeknucPwrCache;
import meknuc.reactor.MeknucPwrMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPwrPart extends TileEntityMultiblock<MeknucPwrMultiblockData> {

    private boolean prevPlaying;

    public TileEntityPwrPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucPwrMultiblockData createMultiblock() {
        return new MeknucPwrMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucPwrMultiblockData> getManager() {
        return MeknucPwrMultiblock.MANAGER;
    }

    @Override
    protected boolean canPlaySound() {
        return shouldPlaySound(getMultiblock());
    }

    @Override
    protected boolean onUpdateServer(MeknucPwrMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        boolean playing = shouldPlaySound(multiblock);
        if (playing != prevPlaying) {
            prevPlaying = playing;
            needsPacket = true;
        }
        return needsPacket;
    }

    private boolean shouldPlaySound(MeknucPwrMultiblockData multiblock) {
        return isMaster() && multiblock.isFormed() && multiblock.isActive() && !multiblock.isMeltedDown()
              && multiblock.getBurnTime() > 0 && !multiblock.isBurnPaused();
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
    protected void structureChanged(MeknucPwrMultiblockData multiblock) {
        super.structureChanged(multiblock);
        if (!isRemote() && !multiblock.isFormed()) {
            UUID id = getCacheID();
            if (id != null) {
                MeknucPwrMultiblock.MANAGER.replaceCaches(Set.of(id), id, new MeknucPwrCache());
            }
        }
    }
}
