package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucHtgrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING =
          registerTooltipBlock("high_temperature_graphite_gas_cooled_reactor_casing",
                () -> new BlockBasicMultiblock<>(MeknucHtgrBlockTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY =
          registerTooltipBlock("high_temperature_graphite_gas_cooled_reactor_fuel_particle_control_assembly",
                () -> new BlockTileModel<>(MeknucHtgrBlockTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED =
          registerTooltipBlock("high_temperature_graphite_gas_cooled_reactor_fuel_pebble_bed",
                () -> new BlockTileModel<>(MeknucHtgrBlockTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT =
          registerTooltipBlock("high_temperature_graphite_gas_cooled_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucHtgrBlockTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER =
          registerTooltipBlock("high_temperature_graphite_gas_cooled_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucHtgrBlockTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER);
    }

    private MeknucHtgrBlocks() {
    }
}
