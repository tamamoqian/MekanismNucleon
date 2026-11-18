package meknuc.modules;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.registration.impl.ModuleRegistryObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 套装 / 已安装单元的公共查询工具。
 */
public final class MeknucSuitModules {

    public static final EquipmentSlot[] SUIT_SLOTS = {
          EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /**
     * 是否穿着全套 Mekanism MekaSuit（头盔、胸甲、护腿、靴子）。
     */
    public static boolean isWearingFullSuit(Player player) {
        for (EquipmentSlot slot : SUIT_SLOTS) {
            if (!(player.getItemBySlot(slot).getItem() instanceof ItemMekaSuitArmor)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在整套装甲中查找第一个已启用（且已安装）的指定单元。
     */
    @Nullable
    public static <MODULE extends ICustomModule<MODULE>> FoundModule<MODULE> findEnabled(Player player, ModuleRegistryObject<MODULE> type) {
        for (EquipmentSlot slot : SUIT_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ItemMekaSuitArmor) {
                IModule<MODULE> module = IModuleHelper.INSTANCE.getIfEnabled(stack, type);
                if (module != null) {
                    return new FoundModule<>(module, stack);
                }
            }
        }
        return null;
    }

    /**
     * 统计整套装甲中已启用单元的总安装数量。
     */
    public static <MODULE extends ICustomModule<MODULE>> int installedCount(Player player, ModuleRegistryObject<MODULE> type) {
        int total = 0;
        for (EquipmentSlot slot : SUIT_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ItemMekaSuitArmor) {
                IModule<MODULE> module = IModuleHelper.INSTANCE.getIfEnabled(stack, type);
                if (module != null) {
                    total += module.getInstalledCount();
                }
            }
        }
        return total;
    }

    public record FoundModule<MODULE extends ICustomModule<MODULE>>(IModule<MODULE> module, ItemStack stack) {
    }

    private MeknucSuitModules() {
    }
}
