package meknuc.modules;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.StorageUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 放射性同位素能量生成单元（仅护腿）：穿戴全套 MekaSuit 时，每个单元每 tick 为套装生产 100,000 FE。
 */
public class ModuleRadioisotopeEnergyUnit implements ICustomModule<ModuleRadioisotopeEnergyUnit> {

    /** 每个单元每 tick 产生的能量（FE）。 */
    public static final long ENERGY_PER_UNIT_PER_TICK = 100_000L;

    public ModuleRadioisotopeEnergyUnit(IModule<ModuleRadioisotopeEnergyUnit> module) {
    }

    @Override
    public void tickServer(IModule<ModuleRadioisotopeEnergyUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        if (!MeknucSuitModules.isWearingFullSuit(player)) {
            return;
        }
        long remaining = ENERGY_PER_UNIT_PER_TICK * module.getInstalledCount();
        for (EquipmentSlot slot : MeknucSuitModules.SUIT_SLOTS) {
            if (remaining <= 0) {
                return;
            }
            ItemStack suitPiece = player.getItemBySlot(slot);
            if (!(suitPiece.getItem() instanceof ItemMekaSuitArmor)) {
                continue;
            }
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(suitPiece, 0);
            if (energyContainer == null) {
                continue;
            }
            long accepted = remaining - energyContainer.insert(remaining, Action.EXECUTE, AutomationType.INTERNAL);
            remaining -= accepted;
        }
    }
}
