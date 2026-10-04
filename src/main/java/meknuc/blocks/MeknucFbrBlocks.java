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

public class MeknucFbrBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockFastBreederReactorCasing, ItemBlockTooltip<MeknucBlockFastBreederReactorCasing>> FAST_BREEDER_REACTOR_CASING =
          register("fast_breeder_reactor_casing", () -> new MeknucBlockFastBreederReactorCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockFastBreederReactorNeutronReflectorCladding, ItemBlockTooltip<MeknucBlockFastBreederReactorNeutronReflectorCladding>> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          register("fast_breeder_reactor_neutron_reflector_cladding", () -> new MeknucBlockFastBreederReactorNeutronReflectorCladding(properties()));

    public static final BlockRegistryObject<MeknucBlockFastBreederReactorRadiationResistantControlAssembly, ItemBlockTooltip<MeknucBlockFastBreederReactorRadiationResistantControlAssembly>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          register("fast_breeder_reactor_radiation_resistant_control_assembly", () -> new MeknucBlockFastBreederReactorRadiationResistantControlAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockFastBreederReactorRadiationResistantFuelAssembly, ItemBlockTooltip<MeknucBlockFastBreederReactorRadiationResistantFuelAssembly>> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          register("fast_breeder_reactor_radiation_resistant_fuel_assembly", () -> new MeknucBlockFastBreederReactorRadiationResistantFuelAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockFastBreederReactorPort, ItemBlockTooltip<MeknucBlockFastBreederReactorPort>> FAST_BREEDER_REACTOR_PORT =
          register("fast_breeder_reactor_port", () -> new MeknucBlockFastBreederReactorPort(MeknucBlockFastBreederReactorPort.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
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
