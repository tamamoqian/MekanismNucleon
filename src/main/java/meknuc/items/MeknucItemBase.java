package meknuc.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import meknuc.meknuc;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MeknucItemBase extends Item {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(meknuc.MODID);
    private static final List<Supplier<ItemStack>> TAB_ITEMS = new ArrayList<>();
    public static final DeferredItem<MeknucItemOreZircon> ZIRCON_ORE_ITEM = register("zircon_ore_item", () -> new MeknucItemOreZircon(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotZircon> ZIRCON_INGOT = register("zircon_ingot", () -> new MeknucItemIngotZircon(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreThorium> THORIUM_ORE_ITEM = register("thorium_ore_item", () -> new MeknucItemOreThorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotThorium> THORIUM_INGOT = register("thorium_ingot", () -> new MeknucItemIngotThorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreBeryllium> BERYLLIUM_ORE_ITEM = register("beryllium_ore_item", () -> new MeknucItemOreBeryllium(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotBeryllium> BERYLLIUM_INGOT = register("beryllium_ingot", () -> new MeknucItemIngotBeryllium(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreBoron> BORON_ORE_ITEM = register("boron_ore_item", () -> new MeknucItemOreBoron(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotBoron> BORON_INGOT = register("boron_ingot", () -> new MeknucItemIngotBoron(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreChrome> CHROME_ORE_ITEM = register("chrome_ore_item", () -> new MeknucItemOreChrome(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotChrome> CHROME_INGOT = register("chrome_ingot", () -> new MeknucItemIngotChrome(new Item.Properties()));
    public static final DeferredItem<MeknucItemEmptyFuelUnit> EMPTY_FUEL_UNIT = register("empty_fuel_unit", () -> new MeknucItemEmptyFuelUnit(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235MoxFuelRod> URANIUM_235_MOX_FUEL_ROD = register("uranium_235_mox_fuel_rod", () -> new MeknucItemUranium235MoxFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235MoxFuelRod> UNSINTERED_URANIUM_235_MOX_FUEL_ROD = register("unsintered_uranium_235_mox_fuel_rod", () -> new MeknucItemUnsinteredUranium235MoxFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUranium235MoxFuelRod> DEPLETED_URANIUM_235_MOX_FUEL_ROD = register("depleted_uranium_235_mox_fuel_rod", () -> new MeknucItemDepletedUranium235MoxFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotUranium235MoxFuelRod> DEPLETED_HOT_URANIUM_235_MOX_FUEL_ROD = register("depleted_hot_uranium_235_mox_fuel_rod", () -> new MeknucItemDepletedHotUranium235MoxFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235ParticleFuel> URANIUM_235_PARTICLE_FUEL = register("uranium_235_particle_fuel", () -> new MeknucItemUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235ParticleFuel> UNSINTERED_URANIUM_235_PARTICLE_FUEL = register("unsintered_uranium_235_particle_fuel", () -> new MeknucItemUnsinteredUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUranium235ParticleFuel> DEPLETED_URANIUM_235_PARTICLE_FUEL = register("depleted_uranium_235_particle_fuel", () -> new MeknucItemDepletedUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotUranium235ParticleFuel> DEPLETED_HOT_URANIUM_235_PARTICLE_FUEL = register("depleted_hot_uranium_235_particle_fuel", () -> new MeknucItemDepletedHotUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235ThoriumFuelRod> URANIUM_235_THORIUM_FUEL_ROD = register("uranium_235_thorium_fuel_rod", () -> new MeknucItemUranium235ThoriumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235ThoriumFuelRod> UNSINTERED_URANIUM_235_THORIUM_FUEL_ROD = register("unsintered_uranium_235_thorium_fuel_rod", () -> new MeknucItemUnsinteredUranium235ThoriumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUranium235ThoriumFuelRod> DEPLETED_URANIUM_235_THORIUM_FUEL_ROD = register("depleted_uranium_235_thorium_fuel_rod", () -> new MeknucItemDepletedUranium235ThoriumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotUranium235ThoriumFuelRod> DEPLETED_HOT_URANIUM_235_THORIUM_FUEL_ROD = register("depleted_hot_uranium_235_thorium_fuel_rod", () -> new MeknucItemDepletedHotUranium235ThoriumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235Plutonium239FuelRod> URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemUranium235Plutonium239FuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235Plutonium239FuelRod> UNSINTERED_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemUnsinteredUranium235Plutonium239FuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUranium235Plutonium239FuelRod> DEPLETED_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("depleted_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemDepletedUranium235Plutonium239FuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotUranium235Plutonium239FuelRod> DEPLETED_HOT_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("depleted_hot_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemDepletedHotUranium235Plutonium239FuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumUraniumPlutoniumFuelRod> THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemThoriumUraniumPlutoniumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredThoriumUraniumPlutoniumFuelRod> UNSINTERED_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("unsintered_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemUnsinteredThoriumUraniumPlutoniumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedThoriumUraniumPlutoniumFuelRod> DEPLETED_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("depleted_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemDepletedThoriumUraniumPlutoniumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotThoriumUraniumPlutoniumFuelRod> DEPLETED_HOT_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("depleted_hot_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemDepletedHotThoriumUraniumPlutoniumFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumOxideParticleFuel> THORIUM_OXIDE_PARTICLE_FUEL = register("thorium_oxide_particle_fuel", () -> new MeknucItemThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredThoriumOxideParticleFuel> UNSINTERED_THORIUM_OXIDE_PARTICLE_FUEL = register("unsintered_thorium_oxide_particle_fuel", () -> new MeknucItemUnsinteredThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedThoriumOxideParticleFuel> DEPLETED_THORIUM_OXIDE_PARTICLE_FUEL = register("depleted_thorium_oxide_particle_fuel", () -> new MeknucItemDepletedThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotThoriumOxideParticleFuel> DEPLETED_HOT_THORIUM_OXIDE_PARTICLE_FUEL = register("depleted_hot_thorium_oxide_particle_fuel", () -> new MeknucItemDepletedHotThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235Plutonium239ZircaloyFuelRod> URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemUranium235Plutonium239ZircaloyFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235Plutonium239ZircaloyFuelRod> UNSINTERED_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemUnsinteredUranium235Plutonium239ZircaloyFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemBredUranium235Plutonium239ZircaloyFuelRod> BRED_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("bred_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemBredUranium235Plutonium239ZircaloyFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemBredHotUranium235Plutonium239ZircaloyFuelRod> BRED_HOT_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("bred_hot_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemBredHotUranium235Plutonium239ZircaloyFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235Plutonium239CarbideFuelRod> URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemUranium235Plutonium239CarbideFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235Plutonium239CarbideFuelRod> UNSINTERED_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemUnsinteredUranium235Plutonium239CarbideFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemBredUranium235Plutonium239CarbideFuelRod> BRED_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("bred_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemBredUranium235Plutonium239CarbideFuelRod(new Item.Properties()));
    public static final DeferredItem<MeknucItemBredHotUranium235Plutonium239CarbideFuelRod> BRED_HOT_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("bred_hot_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemBredHotUranium235Plutonium239CarbideFuelRod(new Item.Properties()));

    public MeknucItemBase(Properties properties) {
        super(properties);
    }

    private static <T extends MeknucItemBase> DeferredItem<T> register(String name, Supplier<T> supplier) {
        DeferredItem<T> deferred = ITEMS.register(name, supplier);
        TAB_ITEMS.add(() -> deferred.get().getDefaultInstance());
        return deferred;
    }

    public static void addTabItems(CreativeModeTab.Output output) {
        TAB_ITEMS.forEach(supplier -> output.accept(supplier.get()));
    }
}
