package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MeknucSpentFuelPoolBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING =
          registerTooltipBlock("spent_fuel_pool_radiation_resistant_casing",
                () -> new BlockBasicMultiblock<>(MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> SPENT_FUEL_POOL_PORT =
          registerTooltipBlock("spent_fuel_pool_port",
                () -> new BlockBasicMultiblock<>(MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityLogicAdapter>>> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          registerTooltipBlock("spent_fuel_pool_logic_adapter",
                () -> new BlockBasicMultiblock<>(MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_LOGIC_ADAPTER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING);
        output.accept(SPENT_FUEL_POOL_PORT);
        output.accept(SPENT_FUEL_POOL_LOGIC_ADAPTER);
    }

    private MeknucSpentFuelPoolBlocks() {
    }
}
