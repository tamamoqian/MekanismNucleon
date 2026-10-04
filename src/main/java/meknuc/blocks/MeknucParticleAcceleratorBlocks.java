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

public class MeknucParticleAcceleratorBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockParticleAcceleratorRadiationResistantCasing, ItemBlockTooltip<MeknucBlockParticleAcceleratorRadiationResistantCasing>> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          register("particle_accelerator_radiation_resistant_casing", () -> new MeknucBlockParticleAcceleratorRadiationResistantCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockParticleAcceleratorSupportFrame, ItemBlockTooltip<MeknucBlockParticleAcceleratorSupportFrame>> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          register("particle_accelerator_support_frame", () -> new MeknucBlockParticleAcceleratorSupportFrame(properties()));

    public static final BlockRegistryObject<MeknucBlockParticleAcceleratorTargetMountingFrame, ItemBlockTooltip<MeknucBlockParticleAcceleratorTargetMountingFrame>> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          register("particle_accelerator_target_mounting_frame", () -> new MeknucBlockParticleAcceleratorTargetMountingFrame(properties()));

    public static final BlockRegistryObject<MeknucBlockParticleAccelerationCoil, ItemBlockTooltip<MeknucBlockParticleAccelerationCoil>> PARTICLE_ACCELERATION_COIL =
          register("particle_acceleration_coil", () -> new MeknucBlockParticleAccelerationCoil(properties()));

    public static final BlockRegistryObject<MeknucBlockParticleAcceleratorPort, ItemBlockTooltip<MeknucBlockParticleAcceleratorPort>> PARTICLE_ACCELERATOR_PORT =
          register("particle_accelerator_port", () -> new MeknucBlockParticleAcceleratorPort(MeknucBlockParticleAcceleratorPort.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING);
        output.accept(PARTICLE_ACCELERATOR_SUPPORT_FRAME);
        output.accept(PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME);
        output.accept(PARTICLE_ACCELERATION_COIL);
        output.accept(PARTICLE_ACCELERATOR_PORT);
    }

    private MeknucParticleAcceleratorBlocks() {
    }
}
