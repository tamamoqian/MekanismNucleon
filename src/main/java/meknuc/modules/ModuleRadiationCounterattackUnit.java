package meknuc.modules;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;

/**
 * 辐射反击单元：穿戴全套 MekaSuit 时，每个单元把受到伤害的 50% 反弹给攻击者（效果由
 * {@link MeknucSuitModuleEvents} 处理）。
 */
public class ModuleRadiationCounterattackUnit implements ICustomModule<ModuleRadiationCounterattackUnit> {

    /** 每个单元反弹的比例。 */
    public static final float REFLECT_RATIO_PER_UNIT = 0.5F;

    public ModuleRadiationCounterattackUnit(IModule<ModuleRadiationCounterattackUnit> module) {
    }
}
