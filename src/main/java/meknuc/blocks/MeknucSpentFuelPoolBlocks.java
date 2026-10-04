package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class MeknucSpentFuelPoolBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockSpentFuelPoolRadiationResistantCasing, ItemBlockTooltip<MeknucBlockSpentFuelPoolRadiationResistantCasing>> SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING =
          register("spent_fuel_pool_radiation_resistant_casing", () -> new MeknucBlockSpentFuelPoolRadiationResistantCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockSpentFuelPoolPort, ItemBlockTooltip<MeknucBlockSpentFuelPoolPort>> SPENT_FUEL_POOL_PORT =
          register("spent_fuel_pool_port", () -> new MeknucBlockSpentFuelPoolPort(MeknucBlockSpentFuelPoolPort.BLOCK_TYPE, properties()));

    public static final BlockRegistryObject<MeknucBlockSpentFuelPoolLogicAdapter, ItemBlockTooltip<MeknucBlockSpentFuelPoolLogicAdapter>> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          register("spent_fuel_pool_logic_adapter", () -> new MeknucBlockSpentFuelPoolLogicAdapter(MeknucBlockSpentFuelPoolLogicAdapter.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING);
        output.accept(SPENT_FUEL_POOL_PORT);
        output.accept(SPENT_FUEL_POOL_LOGIC_ADAPTER);
    }

    private MeknucSpentFuelPoolBlocks() {
    }
}
