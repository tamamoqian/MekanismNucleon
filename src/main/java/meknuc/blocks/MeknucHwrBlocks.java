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

public class MeknucHwrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockHeavyWaterReactorCasing, ItemBlockTooltip<MeknucBlockHeavyWaterReactorCasing>> HEAVY_WATER_REACTOR_CASING =
          register("heavy_water_reactor_casing", () -> new MeknucBlockHeavyWaterReactorCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockHeavyWaterReactorControlAssembly, ItemBlockTooltip<MeknucBlockHeavyWaterReactorControlAssembly>> HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY =
          register("heavy_water_reactor_control_assembly", () -> new MeknucBlockHeavyWaterReactorControlAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockHeavyWaterReactorFuelAssembly, ItemBlockTooltip<MeknucBlockHeavyWaterReactorFuelAssembly>> HEAVY_WATER_REACTOR_FUEL_ASSEMBLY =
          register("heavy_water_reactor_fuel_assembly", () -> new MeknucBlockHeavyWaterReactorFuelAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockHeavyWaterReactorPort, ItemBlockTooltip<MeknucBlockHeavyWaterReactorPort>> HEAVY_WATER_REACTOR_PORT =
          register("heavy_water_reactor_port", () -> new MeknucBlockHeavyWaterReactorPort(MeknucBlockHeavyWaterReactorPort.BLOCK_TYPE, properties()));

    public static final BlockRegistryObject<MeknucBlockHeavyWaterReactorLogicAdapter, ItemBlockTooltip<MeknucBlockHeavyWaterReactorLogicAdapter>> HEAVY_WATER_REACTOR_LOGIC_ADAPTER =
          register("heavy_water_reactor_logic_adapter", () -> new MeknucBlockHeavyWaterReactorLogicAdapter(MeknucBlockHeavyWaterReactorLogicAdapter.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
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
