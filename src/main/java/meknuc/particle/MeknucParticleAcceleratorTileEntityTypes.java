package meknuc.particle;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.Meknuc;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import meknuc.particle.tile.TileEntityParticleAcceleratorPart;
import meknuc.particle.tile.TileEntityParticleAcceleratorPort;

public class MeknucParticleAcceleratorTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorController> PARTICLE_ACCELERATOR_CONTROLLER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_CONTROLLER, TileEntityParticleAcceleratorController::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_SUPPORT_FRAME, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATION_COIL =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATION_COIL, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPort> PARTICLE_ACCELERATOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT, TileEntityParticleAcceleratorPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucParticleAcceleratorTileEntityTypes() {
    }
}
