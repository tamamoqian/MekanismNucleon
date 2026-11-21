package meknuc.modules;

import mekanism.api.MekanismIMC;
import mekanism.api.gear.ModuleData;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import meknuc.Meknuc;
import meknuc.init.MeknucItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * MekaSuit 升级单元（模块）注册。
 */
public class MeknucModules {

    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(Meknuc.MOD_ID);

    // ---------------------------------------------------------------- items

    public static final DeferredItem<ItemModule> MODULE_RADIATION_ISOLATION = MeknucItems.ITEMS.register(
          "module_radiation_isolation_unit",
          () -> new ItemModule(() -> holder(MeknucModules.RADIATION_ISOLATION), new Item.Properties()));

    public static final DeferredItem<ItemModule> MODULE_DEPLETED_URANIUM_ARMOR = MeknucItems.ITEMS.register(
          "module_depleted_uranium_armor_unit",
          () -> new ItemModule(() -> holder(MeknucModules.DEPLETED_URANIUM_ARMOR), new Item.Properties()));

    public static final DeferredItem<ItemModule> MODULE_RADIATION_COUNTERATTACK = MeknucItems.ITEMS.register(
          "module_radiation_counterattack_unit",
          () -> new ItemModule(() -> holder(MeknucModules.RADIATION_COUNTERATTACK), new Item.Properties()));

    public static final DeferredItem<ItemModule> MODULE_NEUTRON_ARMOR = MeknucItems.ITEMS.register(
          "module_neutron_armor_unit",
          () -> new ItemModule(() -> holder(MeknucModules.NEUTRON_ARMOR), new Item.Properties()));

    public static final DeferredItem<ItemModule> MODULE_RADIOISOTOPE_ENERGY_GENERATION = MeknucItems.ITEMS.register(
          "module_radioisotope_energy_generation_unit",
          () -> new ItemModule(() -> holder(MeknucModules.RADIOISOTOPE_ENERGY_GENERATION), new Item.Properties()));

    // ---------------------------------------------------------------- module data

    public static final ModuleRegistryObject<ModuleRadiationIsolationUnit> RADIATION_ISOLATION = MODULES.register(
          "radiation_isolation_unit",
          ModuleRadiationIsolationUnit::new,
          () -> MeknucModules.MODULE_RADIATION_ISOLATION,
          builder -> builder.maxStackSize(1));

    public static final ModuleRegistryObject<ModuleDepletedUraniumArmorUnit> DEPLETED_URANIUM_ARMOR = MODULES.register(
          "depleted_uranium_armor_unit",
          ModuleDepletedUraniumArmorUnit::new,
          () -> MeknucModules.MODULE_DEPLETED_URANIUM_ARMOR,
          builder -> builder.maxStackSize(4));

    public static final ModuleRegistryObject<ModuleRadiationCounterattackUnit> RADIATION_COUNTERATTACK = MODULES.register(
          "radiation_counterattack_unit",
          ModuleRadiationCounterattackUnit::new,
          () -> MeknucModules.MODULE_RADIATION_COUNTERATTACK,
          builder -> builder.maxStackSize(4));

    public static final ModuleRegistryObject<ModuleNeutronArmorUnit> NEUTRON_ARMOR = MODULES.register(
          "neutron_armor_unit",
          ModuleNeutronArmorUnit::new,
          () -> MeknucModules.MODULE_NEUTRON_ARMOR,
          builder -> builder.maxStackSize(1));

    public static final ModuleRegistryObject<ModuleRadioisotopeEnergyUnit> RADIOISOTOPE_ENERGY_GENERATION = MODULES.register(
          "radioisotope_energy_generation_unit",
          ModuleRadioisotopeEnergyUnit::new,
          () -> MeknucModules.MODULE_RADIOISOTOPE_ENERGY_GENERATION,
          builder -> builder.maxStackSize(8));

    @SuppressWarnings("unchecked")
    private static Holder<ModuleData<?>> holder(ModuleRegistryObject<?> module) {
        return (Holder<ModuleData<?>>) (Holder<?>) module;
    }

    /**
     * 告诉 Mekanism 的 MekaSuit 各部件可以安装哪些单元（跨模组 IMC）。
     */
    public static void sendIMC() {
        Holder<ModuleData<?>> radiationIsolation = holder(RADIATION_ISOLATION);
        Holder<ModuleData<?>> depletedArmor = holder(DEPLETED_URANIUM_ARMOR);
        Holder<ModuleData<?>> neutronArmor = holder(NEUTRON_ARMOR);
        Holder<ModuleData<?>> counterattack = holder(RADIATION_COUNTERATTACK);
        Holder<ModuleData<?>> energyGeneration = holder(RADIOISOTOPE_ENERGY_GENERATION);

        MekanismIMC.addMekaSuitHelmetModules(radiationIsolation, depletedArmor, neutronArmor);
        MekanismIMC.addMekaSuitBodyarmorModules(radiationIsolation, depletedArmor, neutronArmor, counterattack);
        MekanismIMC.addMekaSuitPantsModules(radiationIsolation, depletedArmor, neutronArmor, energyGeneration);
        MekanismIMC.addMekaSuitBootsModules(radiationIsolation, depletedArmor, neutronArmor);
    }

    public static void addTabItems(CreativeModeTab.Output output) {
        output.accept(MODULE_RADIATION_ISOLATION);
        output.accept(MODULE_DEPLETED_URANIUM_ARMOR);
        output.accept(MODULE_RADIATION_COUNTERATTACK);
        output.accept(MODULE_NEUTRON_ARMOR);
        output.accept(MODULE_RADIOISOTOPE_ENERGY_GENERATION);
    }

    private MeknucModules() {
    }
}
