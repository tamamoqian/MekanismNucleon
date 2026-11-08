package meknuc.reactor;

import java.util.function.Supplier;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.reactor.tile.TileEntityBwrControlAssembly;
import meknuc.reactor.tile.TileEntityBwrFuelAssembly;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrPart;
import meknuc.reactor.tile.TileEntityBwrPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucBwrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityBwrPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityBwrPart>>> BOILING_WATER_REACTOR_CASING =
          registerTooltipBlock("boiling_water_reactor_casing",
                () -> new BlockBasicMultiblock<>(MeknucBwrBlockTypes.BOILING_WATER_REACTOR_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityBwrPort>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityBwrPort>>> BOILING_WATER_REACTOR_PORT =
          registerTooltipBlock("boiling_water_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucBwrBlockTypes.BOILING_WATER_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityBwrLogicAdapter>,
          ItemBlockBwrLogicAdapter> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          BLOCKS.register("boiling_water_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucBwrBlockTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)),
                ItemBlockBwrLogicAdapter::new);

    public static final BlockRegistryObject<BlockTileModel<TileEntityBwrControlAssembly, BlockTypeTile<TileEntityBwrControlAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityBwrControlAssembly, BlockTypeTile<TileEntityBwrControlAssembly>>>> BOILING_WATER_REACTOR_CONTROL_ASSEMBLY =
          registerTooltipBlock("boiling_water_reactor_control_assembly",
                () -> new BlockTileModel<>(MeknucBwrBlockTypes.BOILING_WATER_REACTOR_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityBwrFuelAssembly, BlockTypeTile<TileEntityBwrFuelAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityBwrFuelAssembly, BlockTypeTile<TileEntityBwrFuelAssembly>>>> BOILING_WATER_REACTOR_FUEL_ASSEMBLY =
          registerTooltipBlock("boiling_water_reactor_fuel_assembly",
                () -> new BlockTileModel<>(MeknucBwrBlockTypes.BOILING_WATER_REACTOR_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(BOILING_WATER_REACTOR_CASING);
        output.accept(BOILING_WATER_REACTOR_CONTROL_ASSEMBLY);
        output.accept(BOILING_WATER_REACTOR_FUEL_ASSEMBLY);
        output.accept(BOILING_WATER_REACTOR_PORT);
        output.accept(BOILING_WATER_REACTOR_LOGIC_ADAPTER);
    }

    private MeknucBwrBlocks() {
    }
}
