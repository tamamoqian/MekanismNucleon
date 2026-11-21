package meknuc.reactor;

import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.reactor.tile.TileEntityFbrPart;

public class MeknucFbrContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucFbrContainer> FAST_BREEDER_REACTOR =
          CONTAINER_TYPES.register("fast_breeder_reactor", TileEntityFbrPart.class, MeknucFbrContainer::new);

    private MeknucFbrContainerTypes() {
    }
}
