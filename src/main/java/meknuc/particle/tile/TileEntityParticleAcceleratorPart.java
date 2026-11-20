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

    /**
     * 停机后继续保留音效的 tick 数（6 秒）。
     * <p>
     * Mekanism 的机器音效一旦 {@link #canPlaySound()} 变 false 就立刻停止，重新开始还要等 1 秒冷却，
     * 于是「缺电 / 缺料 / 两批配方之间」这种一两 tick 的抖动会让两分钟的循环白噪音反复从头播放，
     * 听上去断断续续。这里留一段宽限期，短暂停机时声音继续循环，只有真正停下来才关闭。
     */
    private static final int SOUND_HOLD_TICKS = 120;

    private boolean prevPlaying;
    private int soundHoldTicks;

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
        // 宽限期内即使状态短暂离开「运行中」也继续播放，避免循环音效反复从头开始
        return soundHoldTicks > 0 || shouldPlaySound(getMultiblock());
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
        updateSoundHold();
        if (!canPlaySound()) {
            SoundHandler.stopTileSound(getSoundPos());
        }
    }

    /**
     * 运行中把宽限期重置为满值；停机后逐 tick 递减，归零时才真正停止音效。
     * 结构一旦破坏则立刻清零，不在废墟上继续播放。
     */
    private void updateSoundHold() {
        MeknucParticleAcceleratorMultiblockData multiblock = getMultiblock();
        if (shouldPlaySound(multiblock)) {
            soundHoldTicks = SOUND_HOLD_TICKS;
        } else if (!multiblock.isFormed()) {
            soundHoldTicks = 0;
        } else if (soundHoldTicks > 0) {
            soundHoldTicks--;
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
