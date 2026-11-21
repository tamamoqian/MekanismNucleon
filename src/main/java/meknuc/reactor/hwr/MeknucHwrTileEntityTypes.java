package meknuc.reactor.hwr;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.Meknuc;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;

public class MeknucHwrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HEAVY_WATER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> HEAVY_WATER_REACTOR_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_FUEL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> HEAVY_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> HEAVY_WATER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucHwrTileEntityTypes() {
    }
}
