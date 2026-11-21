package meknuc.modules;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;

/**
 * 贫铀装甲单元：穿戴全套 MekaSuit 时，每个单元提供 10 点伤害减免（最多堆叠 4 个，效果由
 * {@link MeknucSuitModuleEvents} 处理）。
 */
public class ModuleDepletedUraniumArmorUnit implements ICustomModule<ModuleDepletedUraniumArmorUnit> {

    /** 每个单元提供的固定伤害减免。 */
    public static final float DAMAGE_REDUCTION_PER_UNIT = 10.0F;

    public ModuleDepletedUraniumArmorUnit(IModule<ModuleDepletedUraniumArmorUnit> module) {
    }
}
