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
    public static final DeferredItem<MeknucItemFuelRodUranium235Mox> URANIUM_235_MOX_FUEL_ROD = register("uranium_235_mox_fuel_rod", () -> new MeknucItemFuelRodUranium235Mox(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredUranium235Mox> UNSINTERED_URANIUM_235_MOX_FUEL_ROD = register("unsintered_uranium_235_mox_fuel_rod", () -> new MeknucItemFuelRodUnsinteredUranium235Mox(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedUranium235Mox> DEPLETED_URANIUM_235_MOX_FUEL_ROD = register("depleted_uranium_235_mox_fuel_rod", () -> new MeknucItemFuelRodDepletedUranium235Mox(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedHotUranium235Mox> DEPLETED_HOT_URANIUM_235_MOX_FUEL_ROD = register("depleted_hot_uranium_235_mox_fuel_rod", () -> new MeknucItemFuelRodDepletedHotUranium235Mox(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235ParticleFuel> URANIUM_235_PARTICLE_FUEL = register("uranium_235_particle_fuel", () -> new MeknucItemUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredUranium235ParticleFuel> UNSINTERED_URANIUM_235_PARTICLE_FUEL = register("unsintered_uranium_235_particle_fuel", () -> new MeknucItemUnsinteredUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUranium235ParticleFuel> DEPLETED_URANIUM_235_PARTICLE_FUEL = register("depleted_uranium_235_particle_fuel", () -> new MeknucItemDepletedUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotUranium235ParticleFuel> DEPLETED_HOT_URANIUM_235_PARTICLE_FUEL = register("depleted_hot_uranium_235_particle_fuel", () -> new MeknucItemDepletedHotUranium235ParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUranium235Thorium> URANIUM_235_THORIUM_FUEL_ROD = register("uranium_235_thorium_fuel_rod", () -> new MeknucItemFuelRodUranium235Thorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredUranium235Thorium> UNSINTERED_URANIUM_235_THORIUM_FUEL_ROD = register("unsintered_uranium_235_thorium_fuel_rod", () -> new MeknucItemFuelRodUnsinteredUranium235Thorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedUranium235Thorium> DEPLETED_URANIUM_235_THORIUM_FUEL_ROD = register("depleted_uranium_235_thorium_fuel_rod", () -> new MeknucItemFuelRodDepletedUranium235Thorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedHotUranium235Thorium> DEPLETED_HOT_URANIUM_235_THORIUM_FUEL_ROD = register("depleted_hot_uranium_235_thorium_fuel_rod", () -> new MeknucItemFuelRodDepletedHotUranium235Thorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUranium235Plutonium239> URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemFuelRodUranium235Plutonium239(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredUranium235Plutonium239> UNSINTERED_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemFuelRodUnsinteredUranium235Plutonium239(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedUranium235Plutonium239> DEPLETED_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("depleted_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemFuelRodDepletedUranium235Plutonium239(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedHotUranium235Plutonium239> DEPLETED_HOT_URANIUM_235_PLUTONIUM_239_FUEL_ROD = register("depleted_hot_uranium_235_plutonium_239_fuel_rod", () -> new MeknucItemFuelRodDepletedHotUranium235Plutonium239(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodThoriumUraniumPlutonium> THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemFuelRodThoriumUraniumPlutonium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredThoriumUraniumPlutonium> UNSINTERED_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("unsintered_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemFuelRodUnsinteredThoriumUraniumPlutonium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedThoriumUraniumPlutonium> DEPLETED_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("depleted_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemFuelRodDepletedThoriumUraniumPlutonium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodDepletedHotThoriumUraniumPlutonium> DEPLETED_HOT_THORIUM_URANIUM_PLUTONIUM_FUEL_ROD = register("depleted_hot_thorium_uranium_plutonium_fuel_rod", () -> new MeknucItemFuelRodDepletedHotThoriumUraniumPlutonium(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumOxideParticleFuel> THORIUM_OXIDE_PARTICLE_FUEL = register("thorium_oxide_particle_fuel", () -> new MeknucItemThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemUnsinteredThoriumOxideParticleFuel> UNSINTERED_THORIUM_OXIDE_PARTICLE_FUEL = register("unsintered_thorium_oxide_particle_fuel", () -> new MeknucItemUnsinteredThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedThoriumOxideParticleFuel> DEPLETED_THORIUM_OXIDE_PARTICLE_FUEL = register("depleted_thorium_oxide_particle_fuel", () -> new MeknucItemDepletedThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedHotThoriumOxideParticleFuel> DEPLETED_HOT_THORIUM_OXIDE_PARTICLE_FUEL = register("depleted_hot_thorium_oxide_particle_fuel", () -> new MeknucItemDepletedHotThoriumOxideParticleFuel(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUranium235Plutonium239Zircaloy> URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemFuelRodUranium235Plutonium239Zircaloy(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredUranium235Plutonium239Zircaloy> UNSINTERED_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemFuelRodUnsinteredUranium235Plutonium239Zircaloy(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodBredUranium235Plutonium239Zircaloy> BRED_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("bred_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemFuelRodBredUranium235Plutonium239Zircaloy(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodBredHotUranium235Plutonium239Zircaloy> BRED_HOT_URANIUM_235_PLUTONIUM_239_ZIRCALOY_FUEL_ROD = register("bred_hot_uranium_235_plutonium_239_zircaloy_fuel_rod", () -> new MeknucItemFuelRodBredHotUranium235Plutonium239Zircaloy(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUranium235Plutonium239Carbide> URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemFuelRodUranium235Plutonium239Carbide(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodUnsinteredUranium235Plutonium239Carbide> UNSINTERED_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("unsintered_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemFuelRodUnsinteredUranium235Plutonium239Carbide(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodBredUranium235Plutonium239Carbide> BRED_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("bred_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemFuelRodBredUranium235Plutonium239Carbide(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodBredHotUranium235Plutonium239Carbide> BRED_HOT_URANIUM_235_PLUTONIUM_239_CARBIDE_FUEL_ROD = register("bred_hot_uranium_235_plutonium_239_carbide_fuel_rod", () -> new MeknucItemFuelRodBredHotUranium235Plutonium239Carbide(new Item.Properties()));
    public static final DeferredItem<MeknucItemAmericium241Dust> AMERICIUM_241_DUST = register("americium_241_dust",
            () -> new MeknucItemAmericium241Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemBerylliumClump> BERYLLIUM_CLUMP = register("beryllium_clump",
            () -> new MeknucItemBerylliumClump(new Item.Properties()));
    public static final DeferredItem<MeknucItemBerylliumCrystal> BERYLLIUM_CRYSTAL = register("beryllium_crystal",
            () -> new MeknucItemBerylliumCrystal(new Item.Properties()));
    public static final DeferredItem<MeknucItemBerylliumDust> BERYLLIUM_DUST = register("beryllium_dust",
            () -> new MeknucItemBerylliumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemBerylliumShard> BERYLLIUM_SHARD = register("beryllium_shard",
            () -> new MeknucItemBerylliumShard(new Item.Properties()));
    public static final DeferredItem<MeknucItemBoronClump> BORON_CLUMP = register("boron_clump",
            () -> new MeknucItemBoronClump(new Item.Properties()));
    public static final DeferredItem<MeknucItemBoronCrystal> BORON_CRYSTAL = register("boron_crystal",
            () -> new MeknucItemBoronCrystal(new Item.Properties()));
    public static final DeferredItem<MeknucItemBoronDust> BORON_DUST = register("boron_dust",
            () -> new MeknucItemBoronDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemBoronShard> BORON_SHARD = register("boron_shard",
            () -> new MeknucItemBoronShard(new Item.Properties()));
    public static final DeferredItem<MeknucItemChromiumClump> CHROMIUM_CLUMP = register("chromium_clump",
            () -> new MeknucItemChromiumClump(new Item.Properties()));
    public static final DeferredItem<MeknucItemChromiumCrystal> CHROMIUM_CRYSTAL = register("chromium_crystal",
            () -> new MeknucItemChromiumCrystal(new Item.Properties()));
    public static final DeferredItem<MeknucItemChromiumDust> CHROMIUM_DUST = register("chromium_dust",
            () -> new MeknucItemChromiumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemChromiumIngot> CHROMIUM_INGOT = register("chromium_ingot",
            () -> new MeknucItemChromiumIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemChromiumShard> CHROMIUM_SHARD = register("chromium_shard",
            () -> new MeknucItemChromiumShard(new Item.Properties()));
    public static final DeferredItem<MeknucItemCorrosionResistantAlloyIngot> CORROSION_RESISTANT_ALLOY_INGOT = register("corrosion_resistant_alloy_ingot",
            () -> new MeknucItemCorrosionResistantAlloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemDepletedUraniumAlloyIngot> DEPLETED_URANIUM_ALLOY_INGOT = register("depleted_uranium_alloy_ingot",
            () -> new MeknucItemDepletedUraniumAlloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemDirtyBerylliumDust> DIRTY_BERYLLIUM_DUST = register("dirty_beryllium_dust",
            () -> new MeknucItemDirtyBerylliumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemDirtyBoronDust> DIRTY_BORON_DUST = register("dirty_boron_dust",
            () -> new MeknucItemDirtyBoronDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemDirtyChromiumDust> DIRTY_CHROMIUM_DUST = register("dirty_chromium_dust",
            () -> new MeknucItemDirtyChromiumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemDirtyThoriumDust> DIRTY_THORIUM_DUST = register("dirty_thorium_dust",
            () -> new MeknucItemDirtyThoriumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemDirtyZirconiumDust> DIRTY_ZIRCONIUM_DUST = register("dirty_zirconium_dust",
            () -> new MeknucItemDirtyZirconiumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemEnrichedBeryllium> ENRICHED_BERYLLIUM = register("enriched_beryllium",
            () -> new MeknucItemEnrichedBeryllium(new Item.Properties()));
    public static final DeferredItem<MeknucItemEnrichedBoron> ENRICHED_BORON = register("enriched_boron",
            () -> new MeknucItemEnrichedBoron(new Item.Properties()));
    public static final DeferredItem<MeknucItemEnrichedChromium> ENRICHED_CHROMIUM = register("enriched_chromium",
            () -> new MeknucItemEnrichedChromium(new Item.Properties()));
    public static final DeferredItem<MeknucItemEnrichedZirconium> ENRICHED_ZIRCONIUM = register("enriched_zirconium",
            () -> new MeknucItemEnrichedZirconium(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodAssemblyMachine> FUEL_ROD_ASSEMBLY_MACHINE = register("fuel_rod_assembly_machine",
            () -> new MeknucItemFuelRodAssemblyMachine(new Item.Properties()));
    public static final DeferredItem<MeknucItemFuelRodBakingMachine> FUEL_ROD_BAKING_MACHINE = register("fuel_rod_baking_machine",
            () -> new MeknucItemFuelRodBakingMachine(new Item.Properties()));
    public static final DeferredItem<MeknucItemGraphiteIngot> GRAPHITE_INGOT = register("graphite_ingot",
            () -> new MeknucItemGraphiteIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemHighStrengthAlloyDust> HIGH_STRENGTH_ALLOY_DUST = register("high_strength_alloy_dust",
            () -> new MeknucItemHighStrengthAlloyDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemHighStrengthAlloyIngot> HIGH_STRENGTH_ALLOY_INGOT = register("high_strength_alloy_ingot",
            () -> new MeknucItemHighStrengthAlloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemLeadBasedAlloyIngot> LEAD_BASED_ALLOY_INGOT = register("lead_based_alloy_ingot",
            () -> new MeknucItemLeadBasedAlloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemNeutroniumIngot> NEUTRONIUM_INGOT = register("neutronium_ingot",
            () -> new MeknucItemNeutroniumIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemPlutonium239Dust> PLUTONIUM_239_DUST = register("plutonium_239_dust",
            () -> new MeknucItemPlutonium239Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemPolonium210Dust> POLONIUM_210_DUST = register("polonium_210_dust",
            () -> new MeknucItemPolonium210Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemRadiationControlCircuit> RADIATION_CONTROL_CIRCUIT = register("radiation_control_circuit",
            () -> new MeknucItemRadiationControlCircuit(new Item.Properties()));
    public static final DeferredItem<MeknucItemRadiationResistantHighStrengthAlloyIngot> RADIATION_RESISTANT_HIGH_STRENGTH_ALLOY_INGOT = register("radiation_resistant_high_strength_alloy_ingot",
            () -> new MeknucItemRadiationResistantHighStrengthAlloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemRutheniumDust> RUTHENIUM_DUST = register("ruthenium_dust",
            () -> new MeknucItemRutheniumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemStainlessSteelDust> STAINLESS_STEEL_DUST = register("stainless_steel_dust",
            () -> new MeknucItemStainlessSteelDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemStainlessSteelIngot> STAINLESS_STEEL_INGOT = register("stainless_steel_ingot",
            () -> new MeknucItemStainlessSteelIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumClump> THORIUM_CLUMP = register("thorium_clump",
            () -> new MeknucItemThoriumClump(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumCrystal> THORIUM_CRYSTAL = register("thorium_crystal",
            () -> new MeknucItemThoriumCrystal(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumDust> THORIUM_DUST = register("thorium_dust",
            () -> new MeknucItemThoriumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemThoriumShard> THORIUM_SHARD = register("thorium_shard",
            () -> new MeknucItemThoriumShard(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium233Dust> URANIUM_233_DUST = register("uranium_233_dust",
            () -> new MeknucItemUranium233Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium235Dust> URANIUM_235_DUST = register("uranium_235_dust",
            () -> new MeknucItemUranium235Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemUranium238Dust> URANIUM_238_DUST = register("uranium_238_dust",
            () -> new MeknucItemUranium238Dust(new Item.Properties()));
    public static final DeferredItem<MeknucItemZircaloyIngot> ZIRCALOY_INGOT = register("zircaloy_ingot",
            () -> new MeknucItemZircaloyIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemZirconiumClump> ZIRCONIUM_CLUMP = register("zirconium_clump",
            () -> new MeknucItemZirconiumClump(new Item.Properties()));
    public static final DeferredItem<MeknucItemZirconiumCrystal> ZIRCONIUM_CRYSTAL = register("zirconium_crystal",
            () -> new MeknucItemZirconiumCrystal(new Item.Properties()));
    public static final DeferredItem<MeknucItemZirconiumDust> ZIRCONIUM_DUST = register("zirconium_dust",
            () -> new MeknucItemZirconiumDust(new Item.Properties()));
    public static final DeferredItem<MeknucItemZirconiumIngot> ZIRCONIUM_INGOT = register("zirconium_ingot",
            () -> new MeknucItemZirconiumIngot(new Item.Properties()));
    public static final DeferredItem<MeknucItemZirconiumShard> ZIRCONIUM_SHARD = register("zirconium_shard",
            () -> new MeknucItemZirconiumShard(new Item.Properties()));

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
