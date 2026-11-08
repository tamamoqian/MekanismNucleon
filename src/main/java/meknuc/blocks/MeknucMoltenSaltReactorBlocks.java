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

public class MeknucMoltenSaltReactorBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING =
          registerTooltipBlock("thorium_molten_salt_reactor_corrosion_resistant_casing",
                () -> new BlockBasicMultiblock<>(MeknucMoltenSaltReactorBlockTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY =
          registerTooltipBlock("thorium_molten_salt_reactor_corrosion_resistant_control_assembly",
                () -> new BlockTileModel<>(MeknucMoltenSaltReactorBlockTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>,
          ItemBlockTooltip<BlockTileModel<MeknucBlockEntityReactorPart, BlockTypeTile<MeknucBlockEntityReactorPart>>>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY =
          registerTooltipBlock("thorium_molten_salt_reactor_corrosion_resistant_fuel_assembly",
                () -> new BlockTileModel<>(MeknucMoltenSaltReactorBlockTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY,
                      properties -> properties.mapColor(MapColor.DEEPSLATE)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          registerTooltipBlock("thorium_molten_salt_reactor_port",
                () -> new BlockBasicMultiblock<>(MeknucMoltenSaltReactorBlockTypes.THORIUM_MOLTEN_SALT_REACTOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING);
        output.accept(THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY);
        output.accept(THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY);
        output.accept(THORIUM_MOLTEN_SALT_REACTOR_PORT);
    }

    private MeknucMoltenSaltReactorBlocks() {
    }
}
