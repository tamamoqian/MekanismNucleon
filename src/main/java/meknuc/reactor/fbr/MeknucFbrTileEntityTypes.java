package meknuc.reactor.fbr;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.Meknuc;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;

public class MeknucFbrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> FAST_BREEDER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> FAST_BREEDER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucFbrTileEntityTypes() {
    }
}
