package meknuc.reactor.spentpool;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.Meknuc;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolLogicAdapter;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolPort;

public class MeknucSpentFuelPoolTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<TileEntitySpentFuelPoolCasing> SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING, TileEntitySpentFuelPoolCasing::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntitySpentFuelPoolPort> SPENT_FUEL_POOL_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_PORT, TileEntitySpentFuelPoolPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntitySpentFuelPoolLogicAdapter> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_LOGIC_ADAPTER, TileEntitySpentFuelPoolLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucSpentFuelPoolTileEntityTypes() {
    }
}
