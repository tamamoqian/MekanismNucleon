package meknuc.reactor;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.reactor.tile.TileEntityPwrControlAssembly;
import meknuc.reactor.tile.TileEntityPwrFuelAssembly;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrPart;
import meknuc.reactor.tile.TileEntityPwrPort;

public class MeknucPwrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister("meknuc");

    public static final TileEntityTypeRegistryObject<TileEntityPwrPart> PRESSURIZED_WATER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CASING, TileEntityPwrPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPwrPort> PRESSURIZED_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_PORT, TileEntityPwrPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPwrLogicAdapter> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER, TileEntityPwrLogicAdapter::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPwrFuelAssembly> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY, TileEntityPwrFuelAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityPwrControlAssembly> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucPwrBlocks.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY, TileEntityPwrControlAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucPwrTileEntityTypes() {
    }
}
