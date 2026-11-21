package meknuc.reactor.htgr;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.Meknuc;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;

public class MeknucHtgrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucHtgrTileEntityTypes() {
    }
}
