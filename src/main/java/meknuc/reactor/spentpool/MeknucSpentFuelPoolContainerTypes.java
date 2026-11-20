package meknuc.reactor.spentpool;

import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.Meknuc;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;

public class MeknucSpentFuelPoolContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(Meknuc.MOD_ID);

    public static final ContainerTypeRegistryObject<MeknucSpentFuelPoolContainer> SPENT_FUEL_POOL =
          CONTAINER_TYPES.register("spent_fuel_pool", TileEntitySpentFuelPoolCasing.class, MeknucSpentFuelPoolContainer::new);

    private MeknucSpentFuelPoolContainerTypes() {
    }
}
