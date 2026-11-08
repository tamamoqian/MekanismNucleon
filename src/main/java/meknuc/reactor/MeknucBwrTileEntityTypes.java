package meknuc.reactor;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.reactor.tile.TileEntityBwrControlAssembly;
import meknuc.reactor.tile.TileEntityBwrFuelAssembly;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrPart;
import meknuc.reactor.tile.TileEntityBwrPort;

public class MeknucBwrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister("meknuc");

    public static final TileEntityTypeRegistryObject<TileEntityBwrPart> BOILING_WATER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_CASING, TileEntityBwrPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityBwrPort> BOILING_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_PORT, TileEntityBwrPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityBwrLogicAdapter> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_LOGIC_ADAPTER, TileEntityBwrLogicAdapter::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityBwrFuelAssembly> BOILING_WATER_REACTOR_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_FUEL_ASSEMBLY, TileEntityBwrFuelAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityBwrControlAssembly> BOILING_WATER_REACTOR_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_CONTROL_ASSEMBLY, TileEntityBwrControlAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucBwrTileEntityTypes() {
    }
}
