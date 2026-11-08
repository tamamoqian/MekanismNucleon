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

public class MeknucHwrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> HEAVY_WATER_REACTOR_CASING =
          registerTooltipBlock("heavy_water_reactor_casing",
                () -> new BlockBasicMultiblock<>(MeknucHwrBlockTypes.HEAVY_WATER_REACTOR_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY =
          registerTooltipBlock("heavy_water_reactor_control_assembly",
                () -> new BlockTileModel<>(MeknucHwrBlockTypes.HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> HEAVY_WATER_REACTOR_FUEL_ASSEMBLY =
          registerTooltipBlock("heavy_water_reactor_fuel_assembly",
                () -> new BlockTileModel<>(MeknucHwrBlockTypes.HEAVY_WATER_REACTOR_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> HEAVY_WATER_REACTOR_PORT =
          registerTooltipBlock("heavy_water_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucHwrBlockTypes.HEAVY_WATER_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>>> HEAVY_WATER_REACTOR_LOGIC_ADAPTER =
          registerTooltipBlock("heavy_water_reactor_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucHwrBlockTypes.HEAVY_WATER_REACTOR_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(HEAVY_WATER_REACTOR_CASING);
        output.accept(HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY);
        output.accept(HEAVY_WATER_REACTOR_FUEL_ASSEMBLY);
        output.accept(HEAVY_WATER_REACTOR_PORT);
        output.accept(HEAVY_WATER_REACTOR_LOGIC_ADAPTER);
    }

    private MeknucHwrBlocks() {
    }
}
