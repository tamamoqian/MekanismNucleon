package meknuc.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import meknuc.meknuc;
import meknuc.items.MeknucItemBase;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MeknucBlockBase extends Block {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(meknuc.MODID);
    private static final List<Supplier<ItemStack>> TAB_BLOCKS = new ArrayList<>();
    public static final DeferredBlock<MeknucBlockOreZircon> ZIRCON_BLOCK_ORE_BLOCK = register("zircon_ore_block",
            () -> new MeknucBlockOreZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemZircon> ZIRCON_ORE_ITEM_BLOCK = register("zircon_ore_item_block",
            () -> new MeknucBlockOreItemZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockZircon> ZIRCON_BLOCK = register("zircon_block",
            () -> new MeknucBlockZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreThorium> THORIUM_BLOCK_ORE_BLOCK = register("thorium_ore_block",
            () -> new MeknucBlockOreThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemThorium> THORIUM_ORE_ITEM_BLOCK = register("thorium_ore_item_block",
            () -> new MeknucBlockOreItemThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThorium> THORIUM_BLOCK = register("thorium_block",
            () -> new MeknucBlockThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreBeryllium> BERYLLIUM_BLOCK_ORE_BLOCK = register("beryllium_ore_block",
            () -> new MeknucBlockOreBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemBeryllium> BERYLLIUM_ORE_ITEM_BLOCK = register("beryllium_ore_item_block",
            () -> new MeknucBlockOreItemBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBeryllium> BERYLLIUM_BLOCK = register("beryllium_block",
            () -> new MeknucBlockBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreBoron> BORON_BLOCK_ORE_BLOCK = register("boron_ore_block",
            () -> new MeknucBlockOreBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemBoron> BORON_ORE_ITEM_BLOCK = register("boron_ore_item_block",
            () -> new MeknucBlockOreItemBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoron> BORON_BLOCK = register("boron_block",
            () -> new MeknucBlockBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreChrome> CHROME_BLOCK_ORE_BLOCK = register("chrome_ore_block",
            () -> new MeknucBlockOreChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemChrome> CHROME_ORE_ITEM_BLOCK = register("chrome_ore_item_block",
            () -> new MeknucBlockOreItemChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockChrome> CHROME_BLOCK = register("chrome_block",
            () -> new MeknucBlockChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateThorium> THORIUM_DEEPSLATE_ORE_BLOCK = register("thorium_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateZircon> ZIRCON_DEEPSLATE_ORE_BLOCK = register("zircon_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateChrome> CHROME_DEEPSLATE_ORE_BLOCK = register("chrome_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateBoron> BORON_DEEPSLATE_ORE_BLOCK = register("boron_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateBeryllium> BERYLLIUM_DEEPSLATE_ORE_BLOCK = register("beryllium_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockRadioisotopeThermoelectricGenerator> RADIOISOTOPE_THERMOELECTRIC_GENERATOR = register("radioisotope_thermoelectric_generator",
            () -> new MeknucBlockRadioisotopeThermoelectricGenerator(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorCasing> BLOCK_BOILING_WATER_REACTOR_CASING = register("boiling_water_reactor_casing",
            () -> new MeknucBlockBoilingWaterReactorCasing(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorControlAssembly> BLOCK_BOILING_WATER_REACTOR_CONTROL_ASSEMBLY = register("boiling_water_reactor_control_assembly",
            () -> new MeknucBlockBoilingWaterReactorControlAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorCoolantInputPort> BLOCK_BOILING_WATER_REACTOR_COOLANT_INPUT_PORT = register("boiling_water_reactor_coolant_input_port",
            () -> new MeknucBlockBoilingWaterReactorCoolantInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorCoolantOutputPort> BLOCK_BOILING_WATER_REACTOR_COOLANT_OUTPUT_PORT = register("boiling_water_reactor_coolant_output_port",
            () -> new MeknucBlockBoilingWaterReactorCoolantOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorFuelAssembly> BLOCK_BOILING_WATER_REACTOR_FUEL_ASSEMBLY = register("boiling_water_reactor_fuel_assembly",
            () -> new MeknucBlockBoilingWaterReactorFuelAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorFuelInputPort> BLOCK_BOILING_WATER_REACTOR_FUEL_INPUT_PORT = register("boiling_water_reactor_fuel_input_port",
            () -> new MeknucBlockBoilingWaterReactorFuelInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorSignalPort> BLOCK_BOILING_WATER_REACTOR_SIGNAL_PORT = register("boiling_water_reactor_signal_port",
            () -> new MeknucBlockBoilingWaterReactorSignalPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoilingWaterReactorWasteOutputPort> BLOCK_BOILING_WATER_REACTOR_WASTE_OUTPUT_PORT = register("boiling_water_reactor_waste_output_port",
            () -> new MeknucBlockBoilingWaterReactorWasteOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockDecayProductOutputPort> BLOCK_DECAY_PRODUCT_OUTPUT_PORT = register("decay_product_output_port",
            () -> new MeknucBlockDecayProductOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorCasing> BLOCK_HEAVY_WATER_REACTOR_CASING = register("heavy_water_reactor_casing",
            () -> new MeknucBlockHeavyWaterReactorCasing(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorControlAssembly> BLOCK_HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY = register("heavy_water_reactor_control_assembly",
            () -> new MeknucBlockHeavyWaterReactorControlAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorCoolantInputPort> BLOCK_HEAVY_WATER_REACTOR_COOLANT_INPUT_PORT = register("heavy_water_reactor_coolant_input_port",
            () -> new MeknucBlockHeavyWaterReactorCoolantInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorCoolantOutputPort> BLOCK_HEAVY_WATER_REACTOR_COOLANT_OUTPUT_PORT = register("heavy_water_reactor_coolant_output_port",
            () -> new MeknucBlockHeavyWaterReactorCoolantOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorFuelAssembly> BLOCK_HEAVY_WATER_REACTOR_FUEL_ASSEMBLY = register("heavy_water_reactor_fuel_assembly",
            () -> new MeknucBlockHeavyWaterReactorFuelAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorFuelInputPort> BLOCK_HEAVY_WATER_REACTOR_FUEL_INPUT_PORT = register("heavy_water_reactor_fuel_input_port",
            () -> new MeknucBlockHeavyWaterReactorFuelInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorSignalPort> BLOCK_HEAVY_WATER_REACTOR_SIGNAL_PORT = register("heavy_water_reactor_signal_port",
            () -> new MeknucBlockHeavyWaterReactorSignalPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockHeavyWaterReactorWasteOutputPort> BLOCK_HEAVY_WATER_REACTOR_WASTE_OUTPUT_PORT = register("heavy_water_reactor_waste_output_port",
            () -> new MeknucBlockHeavyWaterReactorWasteOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockPressurizedWaterReactorCoolantInputPort> BLOCK_PRESSURIZED_WATER_REACTOR_COOLANT_INPUT_PORT = register("pressurized_water_reactor_coolant_input_port",
            () -> new MeknucBlockPressurizedWaterReactorCoolantInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockPressurizedWaterReactorCoolantOutputPort> BLOCK_PRESSURIZED_WATER_REACTOR_COOLANT_OUTPUT_PORT = register("pressurized_water_reactor_coolant_output_port",
            () -> new MeknucBlockPressurizedWaterReactorCoolantOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockPressurizedWaterReactorFuelInputPort> BLOCK_PRESSURIZED_WATER_REACTOR_FUEL_INPUT_PORT = register("pressurized_water_reactor_fuel_input_port",
            () -> new MeknucBlockPressurizedWaterReactorFuelInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockPressurizedWaterReactorSignalPort> BLOCK_PRESSURIZED_WATER_REACTOR_SIGNAL_PORT = register("pressurized_water_reactor_signal_port",
            () -> new MeknucBlockPressurizedWaterReactorSignalPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockPressurizedWaterReactorWasteOutputPort> BLOCK_PRESSURIZED_WATER_REACTOR_WASTE_OUTPUT_PORT = register("pressurized_water_reactor_waste_output_port",
            () -> new MeknucBlockPressurizedWaterReactorWasteOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockRadiationResistantWasteStorageCasing> BLOCK_RADIATION_RESISTANT_WASTE_STORAGE_CASING = register("radiation_resistant_waste_storage_casing",
            () -> new MeknucBlockRadiationResistantWasteStorageCasing(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockRadioactiveWasteInputPort> BLOCK_RADIOACTIVE_WASTE_INPUT_PORT = register("radioactive_waste_input_port",
            () -> new MeknucBlockRadioactiveWasteInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolCoolantInputPort> BLOCK_SPENT_FUEL_POOL_COOLANT_INPUT_PORT = register("spent_fuel_pool_coolant_input_port",
            () -> new MeknucBlockSpentFuelPoolCoolantInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolCoolantOutputPort> BLOCK_SPENT_FUEL_POOL_COOLANT_OUTPUT_PORT = register("spent_fuel_pool_coolant_output_port",
            () -> new MeknucBlockSpentFuelPoolCoolantOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolRadiationResistantCasing> BLOCK_SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING = register("spent_fuel_pool_radiation_resistant_casing",
            () -> new MeknucBlockSpentFuelPoolRadiationResistantCasing(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolSignalPort> BLOCK_SPENT_FUEL_POOL_SIGNAL_PORT = register("spent_fuel_pool_signal_port",
            () -> new MeknucBlockSpentFuelPoolSignalPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolWasteInputPort> BLOCK_SPENT_FUEL_POOL_WASTE_INPUT_PORT = register("spent_fuel_pool_waste_input_port",
            () -> new MeknucBlockSpentFuelPoolWasteInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockSpentFuelPoolWasteOutputPort> BLOCK_SPENT_FUEL_POOL_WASTE_OUTPUT_PORT = register("spent_fuel_pool_waste_output_port",
            () -> new MeknucBlockSpentFuelPoolWasteOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThoriumMoltenSaltReactorBoilingCoolantFissionWasteOutputPort> BLOCK_THORIUM_MOLTEN_SALT_REACTOR_BOILING_COOLANT_FISSION_WASTE_OUTPUT_PORT = register("thorium_molten_salt_reactor_boiling_coolant_fission_waste_output_port",
            () -> new MeknucBlockThoriumMoltenSaltReactorBoilingCoolantFissionWasteOutputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantCasing> BLOCK_THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING = register("thorium_molten_salt_reactor_corrosion_resistant_casing",
            () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantCasing(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantControlAssembly> BLOCK_THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY = register("thorium_molten_salt_reactor_corrosion_resistant_control_assembly",
            () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantControlAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantFuelAssembly> BLOCK_THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY = register("thorium_molten_salt_reactor_corrosion_resistant_fuel_assembly",
            () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantFuelAssembly(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThoriumMoltenSaltReactorMoltenCoolantFissionFuelCombinedInputPort> BLOCK_THORIUM_MOLTEN_SALT_REACTOR_MOLTEN_COOLANT_FISSION_FUEL_COMBINED_INPUT_PORT = register("thorium_molten_salt_reactor_molten_coolant_fission_fuel_combined_input_port",
            () -> new MeknucBlockThoriumMoltenSaltReactorMoltenCoolantFissionFuelCombinedInputPort(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));

    public MeknucBlockBase(Properties properties) {
        super(properties);
    }

    private static <T extends MeknucBlockBase> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        DeferredItem<BlockItem> blockItem = MeknucItemBase.ITEMS.registerSimpleBlockItem(name, block);
        TAB_BLOCKS.add(() -> blockItem.get().getDefaultInstance());
        return block;
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        TAB_BLOCKS.forEach(supplier -> output.accept(supplier.get()));
    }
}
