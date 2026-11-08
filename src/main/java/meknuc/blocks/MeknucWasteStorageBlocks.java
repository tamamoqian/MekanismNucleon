package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucWasteStorageBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          registerTooltipBlock("radiation_resistant_waste_storage_casing",
                () -> new BlockBasicMultiblock<>(MeknucWasteStorageBlockTypes.RADIATION_RESISTANT_WASTE_STORAGE_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          registerTooltipBlock("radiation_resistant_waste_storage_port",
                () -> new BlockBasicMultiblock<>(MeknucWasteStorageBlockTypes.RADIATION_RESISTANT_WASTE_STORAGE_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(RADIATION_RESISTANT_WASTE_STORAGE_CASING);
        output.accept(RADIATION_RESISTANT_WASTE_STORAGE_PORT);
    }

    private MeknucWasteStorageBlocks() {
    }
}
