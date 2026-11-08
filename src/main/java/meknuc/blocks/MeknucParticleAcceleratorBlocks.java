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

public class MeknucParticleAcceleratorBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          registerTooltipBlock("particle_accelerator_radiation_resistant_casing",
                () -> new BlockBasicMultiblock<>(MeknucParticleAcceleratorBlockTypes.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          registerTooltipBlock("particle_accelerator_support_frame",
                () -> new BlockBasicMultiblock<>(MeknucParticleAcceleratorBlockTypes.PARTICLE_ACCELERATOR_SUPPORT_FRAME,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          registerTooltipBlock("particle_accelerator_target_mounting_frame",
                () -> new BlockBasicMultiblock<>(MeknucParticleAcceleratorBlockTypes.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPart>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPart>>> PARTICLE_ACCELERATION_COIL =
          registerTooltipBlock("particle_acceleration_coil",
                () -> new BlockBasicMultiblock<>(MeknucParticleAcceleratorBlockTypes.PARTICLE_ACCELERATION_COIL,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockBasicMultiblock<MeknucBlockEntityReactorPort>,
          ItemBlockTooltip<BlockBasicMultiblock<MeknucBlockEntityReactorPort>>> PARTICLE_ACCELERATOR_PORT =
          registerTooltipBlock("particle_accelerator_port",
                () -> new BlockBasicMultiblock<>(MeknucParticleAcceleratorBlockTypes.PARTICLE_ACCELERATOR_PORT,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
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
