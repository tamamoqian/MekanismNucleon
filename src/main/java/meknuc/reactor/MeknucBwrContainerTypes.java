package meknuc.reactor;

import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrPart;

public class MeknucBwrContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("meknuc");

    public static final ContainerTypeRegistryObject<MeknucBwrContainer> BOILING_WATER_REACTOR =
          CONTAINER_TYPES.register("boiling_water_reactor", TileEntityBwrPart.class,
                MeknucBwrContainer::new);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBwrLogicAdapter>> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          CONTAINER_TYPES.registerEmpty("boiling_water_reactor_logic_adapter",
                TileEntityBwrLogicAdapter.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBwrPart>> BOILING_WATER_REACTOR_STATS =
          CONTAINER_TYPES.registerEmpty("boiling_water_reactor_stats",
                TileEntityBwrPart.class);

    private MeknucBwrContainerTypes() {
    }
}
