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

public class MeknucHtgrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockHighTemperatureGraphiteGasCooledReactorCasing, ItemBlockTooltip<MeknucBlockHighTemperatureGraphiteGasCooledReactorCasing>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING =
          register("high_temperature_graphite_gas_cooled_reactor_casing", () -> new MeknucBlockHighTemperatureGraphiteGasCooledReactorCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelParticleControlAssembly, ItemBlockTooltip<MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelParticleControlAssembly>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY =
          register("high_temperature_graphite_gas_cooled_reactor_fuel_particle_control_assembly", () -> new MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelParticleControlAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelPebbleBed, ItemBlockTooltip<MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelPebbleBed>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED =
          register("high_temperature_graphite_gas_cooled_reactor_fuel_pebble_bed", () -> new MeknucBlockHighTemperatureGraphiteGasCooledReactorFuelPebbleBed(properties()));

    public static final BlockRegistryObject<MeknucBlockHighTemperatureGraphiteGasCooledReactorPort, ItemBlockTooltip<MeknucBlockHighTemperatureGraphiteGasCooledReactorPort>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT =
          register("high_temperature_graphite_gas_cooled_reactor_port", () -> new MeknucBlockHighTemperatureGraphiteGasCooledReactorPort(MeknucBlockHighTemperatureGraphiteGasCooledReactorPort.BLOCK_TYPE, properties()));

    public static final BlockRegistryObject<MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter, ItemBlockTooltip<MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter>> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER =
          register("high_temperature_graphite_gas_cooled_reactor_logic_adapter", () -> new MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter(MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT);
        output.accept(HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER);
    }

    private MeknucHtgrBlocks() {
    }
}
