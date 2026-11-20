package meknuc.particle.tile;

import java.util.Set;
import java.util.UUID;
import mekanism.client.sound.SoundHandler;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import meknuc.particle.MeknucParticleAcceleratorCache;
import meknuc.particle.MeknucParticleAcceleratorMultiblock;
import meknuc.particle.MeknucParticleAcceleratorMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 大型环形粒子加速器的结构方块实体（外壳 / 支撑框架 / 靶件框架 / 线圈 / 端口共用）。
 * <p>
 * 只有控制器可以作为多方块主方块；这里负责主方块上的运行音效与拆结构时的缓存回收。
 */
public class TileEntityParticleAcceleratorPart extends TileEntityMultiblock<MeknucParticleAcceleratorMultiblockData> {

    private boolean prevPlaying;

    public TileEntityParticleAcceleratorPart(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public MeknucParticleAcceleratorMultiblockData createMultiblock() {
        return new MeknucParticleAcceleratorMultiblockData(this);
    }

    @Override
    public MultiblockManager<MeknucParticleAcceleratorMultiblockData> getManager() {
        return MeknucParticleAcceleratorMultiblock.MANAGER;
    }

    @Override
    public boolean canBeMaster() {
        return false;
    }

    @Override
    protected boolean canPlaySound() {
        return shouldPlaySound(getMultiblock());
    }

    @Override
    protected boolean onUpdateServer(MeknucParticleAcceleratorMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        boolean playing = shouldPlaySound(multiblock);
        if (playing != prevPlaying) {
            prevPlaying = playing;
            needsPacket = true;
        }
        return needsPacket;
    }

    private boolean shouldPlaySound(MeknucParticleAcceleratorMultiblockData multiblock) {
        return isMaster() && multiblock.isFormed()
              && multiblock.getStatus() == MeknucParticleAcceleratorMultiblockData.STATUS_RUNNING;
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
    protected void structureChanged(MeknucParticleAcceleratorMultiblockData multiblock) {
        super.structureChanged(multiblock);
        if (!isRemote() && isMaster()) {
            // 主方块（控制器）登记自己，多方块里的速度 / 能量升级都从它身上读取
            multiblock.setUpgradeTile(this);
        }
        if (!isRemote() && !multiblock.isFormed()) {
            UUID id = getCacheID();
            if (id != null) {
                MeknucParticleAcceleratorMultiblock.MANAGER.replaceCaches(Set.of(id), id,
                      new MeknucParticleAcceleratorCache());
            }
        }
    }
}
