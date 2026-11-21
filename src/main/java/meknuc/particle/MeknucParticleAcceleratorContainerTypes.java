package meknuc.particle;

import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;

public class MeknucParticleAcceleratorContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucParticleAcceleratorContainer> PARTICLE_ACCELERATOR =
          CONTAINER_TYPES.register("particle_accelerator", TileEntityParticleAcceleratorController.class,
                MeknucParticleAcceleratorContainer::new);

    private MeknucParticleAcceleratorContainerTypes() {
    }
}
