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

public class MeknucBwrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockBoilingWaterReactorCasing, ItemBlockTooltip<MeknucBlockBoilingWaterReactorCasing>> BOILING_WATER_REACTOR_CASING =
          register("boiling_water_reactor_casing", () -> new MeknucBlockBoilingWaterReactorCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockBoilingWaterReactorControlAssembly, ItemBlockTooltip<MeknucBlockBoilingWaterReactorControlAssembly>> BOILING_WATER_REACTOR_CONTROL_ASSEMBLY =
          register("boiling_water_reactor_control_assembly", () -> new MeknucBlockBoilingWaterReactorControlAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockBoilingWaterReactorFuelAssembly, ItemBlockTooltip<MeknucBlockBoilingWaterReactorFuelAssembly>> BOILING_WATER_REACTOR_FUEL_ASSEMBLY =
          register("boiling_water_reactor_fuel_assembly", () -> new MeknucBlockBoilingWaterReactorFuelAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockBoilingWaterReactorPort, ItemBlockTooltip<MeknucBlockBoilingWaterReactorPort>> BOILING_WATER_REACTOR_PORT =
          register("boiling_water_reactor_port", () -> new MeknucBlockBoilingWaterReactorPort(MeknucBlockBoilingWaterReactorPort.BLOCK_TYPE, properties()));

    public static final BlockRegistryObject<MeknucBlockBoilingWaterReactorLogicAdapter, ItemBlockTooltip<MeknucBlockBoilingWaterReactorLogicAdapter>> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          register("boiling_water_reactor_logic_adapter", () -> new MeknucBlockBoilingWaterReactorLogicAdapter(MeknucBlockBoilingWaterReactorLogicAdapter.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
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
