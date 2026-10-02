package meknuc.reactor;

import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorLogicAdapter;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;

public class MeknucReactorContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucReactorContainer> PRESSURIZED_WATER_REACTOR =
          CONTAINER_TYPES.register("pressurized_water_reactor", TileEntityPressurizedWaterReactorPart.class,
                MeknucReactorContainer::new);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityPressurizedWaterReactorLogicAdapter>> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          CONTAINER_TYPES.registerEmpty(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                TileEntityPressurizedWaterReactorLogicAdapter.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityPressurizedWaterReactorPart>> PRESSURIZED_WATER_REACTOR_STATS =
          CONTAINER_TYPES.registerEmpty("pressurized_water_reactor_stats",
                TileEntityPressurizedWaterReactorPart.class);

    private MeknucReactorContainerTypes() {
    }
}
