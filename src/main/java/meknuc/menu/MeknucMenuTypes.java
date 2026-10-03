package meknuc.menu;

import meknuc.blockentities.MeknucBlockEntityRTG;
import meknuc.meknuc;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class MeknucMenuTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(meknuc.MODID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<MeknucBlockEntityRTG>> RTG =
          CONTAINER_TYPES.register("radioisotope_thermoelectric_generator", MeknucBlockEntityRTG.class);
}
