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

public class MeknucMoltenSaltReactorBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantCasing, ItemBlockTooltip<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantCasing>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING =
          register("thorium_molten_salt_reactor_corrosion_resistant_casing", () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantControlAssembly, ItemBlockTooltip<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantControlAssembly>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY =
          register("thorium_molten_salt_reactor_corrosion_resistant_control_assembly", () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantControlAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantFuelAssembly, ItemBlockTooltip<MeknucBlockThoriumMoltenSaltReactorCorrosionResistantFuelAssembly>> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY =
          register("thorium_molten_salt_reactor_corrosion_resistant_fuel_assembly", () -> new MeknucBlockThoriumMoltenSaltReactorCorrosionResistantFuelAssembly(properties()));

    public static final BlockRegistryObject<MeknucBlockThoriumMoltenSaltReactorPort, ItemBlockTooltip<MeknucBlockThoriumMoltenSaltReactorPort>> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          register("thorium_molten_salt_reactor_port", () -> new MeknucBlockThoriumMoltenSaltReactorPort(MeknucBlockThoriumMoltenSaltReactorPort.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
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
