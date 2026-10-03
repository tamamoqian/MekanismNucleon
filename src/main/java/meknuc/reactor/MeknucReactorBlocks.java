package meknuc.reactor;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorControlAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorFuelAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorLogicAdapter;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.MapColor;

public class MeknucReactorBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPart>>> PRESSURIZED_WATER_REACTOR_CASING =
          registerPart("pressurized_water_reactor_casing", MeknucReactorBlockTypes.PRESSURIZED_WATER_REACTOR_CASING);

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPort>>> PRESSURIZED_WATER_REACTOR_PORT =
          BLOCKS.registerDetails("pressurized_water_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucReactorBlockTypes.PRESSURIZED_WATER_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPressurizedWaterReactorLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPressurizedWaterReactorLogicAdapter>>> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          BLOCKS.registerDetails("pressurized_water_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucReactorBlockTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityPressurizedWaterReactorFuelAssembly, BlockTypeTile<TileEntityPressurizedWaterReactorFuelAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityPressurizedWaterReactorFuelAssembly, BlockTypeTile<TileEntityPressurizedWaterReactorFuelAssembly>>>> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          BLOCKS.registerDetails("pressurized_water_reactor_fuel_assembly",
                () -> new BlockTileModel<>(MeknucReactorBlockTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityPressurizedWaterReactorControlAssembly, BlockTypeTile<TileEntityPressurizedWaterReactorControlAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityPressurizedWaterReactorControlAssembly, BlockTypeTile<TileEntityPressurizedWaterReactorControlAssembly>>>> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          BLOCKS.registerDetails("pressurized_water_reactor_control_assembly",
                () -> new BlockTileModel<>(MeknucReactorBlockTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    private static BlockRegistryObject<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPressurizedWaterReactorPart>>> registerPart(
          String name, BlockTypeTile<TileEntityPressurizedWaterReactorPart> type) {
        return BLOCKS.registerDetails(name,
              () -> new BlockBasicMultiblock<>(type, properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(PRESSURIZED_WATER_REACTOR_CASING);
        output.accept(PRESSURIZED_WATER_REACTOR_PORT);
        output.accept(PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER);
        output.accept(PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY);
        output.accept(PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);
    }

    private MeknucReactorBlocks() {
    }
}
