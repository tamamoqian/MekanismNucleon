package meknuc.turbine;

import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.generators.common.content.turbine.TurbineCache;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;

/**
 * 高压蒸汽涡轮的多方块管理器。
 * <p>
 * 结构校验沿用 Mekanism 工业涡轮的规则，但外壳 / 端口 / 分压元件 / 冷凝器
 * 全部换成本模组的高压方块；数据与方块实体直接复用 Mekanism 的涡轮体系，
 * 因此 GUI、容器和渲染都能直接使用 Mekanism 的工业涡轮界面。
 */
public class MeknucTurbineMultiblock {

    public static final String NAME = "highPressureTurbine";

    public static final MultiblockManager<TurbineMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, TurbineCache::new, MeknucTurbineValidator::new);

    /**
     * 触发类加载，让静态的 {@link #MANAGER} 注册进 Mekanism 的管理器列表。
     */
    public static void initialize() {
    }

    private MeknucTurbineMultiblock() {
    }
}
