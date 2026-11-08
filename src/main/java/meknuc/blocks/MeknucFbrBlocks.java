package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucFbrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> FAST_BREEDER_REACTOR_CASING =
          registerTooltipBlock("fast_breeder_reactor_casing",
                () -> new BlockBasicMultiblock<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          registerTooltipBlock("fast_breeder_reactor_neutron_reflector_cladding",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          registerTooltipBlock("fast_breeder_reactor_radiation_resistant_control_assembly",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          registerTooltipBlock("fast_breeder_reactor_radiation_resistant_fuel_assembly",
                () -> new BlockTileModel<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> FAST_BREEDER_REACTOR_PORT =
          registerTooltipBlock("fast_breeder_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_PORT,
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
    }

    private MeknucFbrBlocks() {
    }
}
