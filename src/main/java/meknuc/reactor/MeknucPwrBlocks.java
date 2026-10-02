package meknuc.reactor;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.reactor.tile.TileEntityPwrControlAssembly;
import meknuc.reactor.tile.TileEntityPwrFuelAssembly;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrPart;
import meknuc.reactor.tile.TileEntityPwrPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.MapColor;

public class MeknucPwrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPwrPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPwrPart>>> PRESSURIZED_WATER_REACTOR_CASING =
          registerPart("pressurized_water_reactor_casing", MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_CASING);

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPwrPort>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPwrPort>>> PRESSURIZED_WATER_REACTOR_PORT =
          BLOCKS.registerDetails("pressurized_water_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityPwrLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPwrLogicAdapter>>> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          BLOCKS.registerDetails("pressurized_water_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityPwrFuelAssembly, BlockTypeTile<TileEntityPwrFuelAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityPwrFuelAssembly, BlockTypeTile<TileEntityPwrFuelAssembly>>>> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          BLOCKS.registerDetails("pressurized_water_reactor_fuel_assembly",
                () -> new BlockTileModel<>(MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityPwrControlAssembly, BlockTypeTile<TileEntityPwrControlAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityPwrControlAssembly, BlockTypeTile<TileEntityPwrControlAssembly>>>> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          BLOCKS.registerDetails("pressurized_water_reactor_control_assembly",
                () -> new BlockTileModel<>(MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    private static BlockRegistryObject<BlockBasicMultiblock<TileEntityPwrPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityPwrPart>>> registerPart(
          String name, BlockTypeTile<TileEntityPwrPart> type) {
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

    private MeknucPwrBlocks() {
    }
}
