package meknuc.wastestorage;

import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.wastestorage.tile.TileEntityWasteStorageCasing;

public class MeknucWasteStorageContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucWasteStorageContainer> WASTE_STORAGE =
          CONTAINER_TYPES.register("waste_storage", TileEntityWasteStorageCasing.class,
                MeknucWasteStorageContainer::new);

    private MeknucWasteStorageContainerTypes() {
    }
}
