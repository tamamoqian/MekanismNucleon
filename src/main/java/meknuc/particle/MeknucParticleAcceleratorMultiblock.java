package meknuc.particle;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockManager;
/**
 * 大型环形粒子对撞机的多方块管理器。
 * <p>
 * 结构骨架沿用 GTQT 的「大型环形粒子对撞研究复合体」：47 x 7 x 47 的平躺圆环，
 * 方块替换为 Nucleon 自己的外壳 / 线圈 / 钢质机壳 / 反应堆玻璃。
 */
public class MeknucParticleAcceleratorMultiblock {

    public static final String NAME = "particleAccelerator";

    public static final MultiblockManager<MeknucParticleAcceleratorMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucParticleAcceleratorCache::new, MeknucParticleAcceleratorValidator::new);

    /**
     * 只是用来触发类加载，让静态的 {@link #MANAGER} 注册进 Mekanism 的管理器列表。
     */
    public static void initialize() {
        // 静态字段初始化时已完成注册，这里保持空实现供 mod 构造器调用
    }

    private MeknucParticleAcceleratorMultiblock() {
    }
}
