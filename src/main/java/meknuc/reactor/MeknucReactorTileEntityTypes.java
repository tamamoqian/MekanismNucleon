package meknuc.reactor;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorControlAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorFuelAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPort;

public class MeknucReactorTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister("meknuc");

    public static final TileEntityTypeRegistryObject<TileEntityPressurizedWaterReactorPart> PRESSURIZED_WATER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CASING, TileEntityPressurizedWaterReactorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPressurizedWaterReactorPort> PRESSURIZED_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_PORT, TileEntityPressurizedWaterReactorPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPressurizedWaterReactorPart> PRESSURIZED_WATER_REACTOR_SIGNAL_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_SIGNAL_PORT, TileEntityPressurizedWaterReactorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPressurizedWaterReactorFuelAssembly> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY, TileEntityPressurizedWaterReactorFuelAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPressurizedWaterReactorControlAssembly> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucReactorBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY, TileEntityPressurizedWaterReactorControlAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucReactorTileEntityTypes() {
    }
}
