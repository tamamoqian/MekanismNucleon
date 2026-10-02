package meknuc.reactor;

import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrPart;

public class MeknucPwrContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucPwrContainer> PRESSURIZED_WATER_REACTOR =
          CONTAINER_TYPES.register("pressurized_water_reactor", TileEntityPwrPart.class,
                MeknucPwrContainer::new);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityPwrLogicAdapter>> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          CONTAINER_TYPES.registerEmpty(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                TileEntityPwrLogicAdapter.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityPwrPart>> PRESSURIZED_WATER_REACTOR_STATS =
          CONTAINER_TYPES.registerEmpty("pressurized_water_reactor_stats",
                TileEntityPwrPart.class);

    private MeknucPwrContainerTypes() {
    }
}
