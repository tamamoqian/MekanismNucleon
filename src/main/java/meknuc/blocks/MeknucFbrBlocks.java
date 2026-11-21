package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.reactor.tile.TileEntityFbrControlAssembly;
import meknuc.reactor.tile.TileEntityFbrFuelAssembly;
import meknuc.reactor.tile.TileEntityFbrLogicAdapter;
import meknuc.reactor.tile.TileEntityFbrPart;
import meknuc.reactor.tile.TileEntityFbrPort;
import meknuc.reactor.tile.TileEntityFbrReflectorCladding;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucFbrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityFbrPart>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityFbrPart>>> FAST_BREEDER_REACTOR_CASING =
          registerTooltipBlock("fast_breeder_reactor_casing",
                () -> new BlockBasicMultiblock<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityFbrReflectorCladding, BlockTypeTile<TileEntityFbrReflectorCladding>>,
          ItemBlockTooltip<BlockTileModel<TileEntityFbrReflectorCladding, BlockTypeTile<TileEntityFbrReflectorCladding>>>> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          registerTooltipBlock("fast_breeder_reactor_neutron_reflector_cladding",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityFbrControlAssembly, BlockTypeTile<TileEntityFbrControlAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityFbrControlAssembly, BlockTypeTile<TileEntityFbrControlAssembly>>>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          registerTooltipBlock("fast_breeder_reactor_radiation_resistant_control_assembly",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityFbrFuelAssembly, BlockTypeTile<TileEntityFbrFuelAssembly>>,
          ItemBlockTooltip<BlockTileModel<TileEntityFbrFuelAssembly, BlockTypeTile<TileEntityFbrFuelAssembly>>>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          registerTooltipBlock("fast_breeder_reactor_radiation_resistant_fuel_assembly",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityFbrPort>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityFbrPort>>> FAST_BREEDER_REACTOR_PORT =
          registerTooltipBlock("fast_breeder_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityFbrLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityFbrLogicAdapter>>> FAST_BREEDER_REACTOR_LOGIC_ADAPTER =
          registerTooltipBlock("fast_breeder_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(FAST_BREEDER_REACTOR_CASING);
        output.accept(FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING);
        output.accept(FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY);
        output.accept(FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY);
        output.accept(FAST_BREEDER_REACTOR_PORT);
        output.accept(FAST_BREEDER_REACTOR_LOGIC_ADAPTER);
    }

    private MeknucFbrBlocks() {
    }
}
