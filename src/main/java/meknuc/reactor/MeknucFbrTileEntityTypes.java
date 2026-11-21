package meknuc.reactor;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.MeknucFbrBlocks;
import meknuc.reactor.tile.TileEntityFbrControlAssembly;
import meknuc.reactor.tile.TileEntityFbrFuelAssembly;
import meknuc.reactor.tile.TileEntityFbrLogicAdapter;
import meknuc.reactor.tile.TileEntityFbrPart;
import meknuc.reactor.tile.TileEntityFbrPort;
import meknuc.reactor.tile.TileEntityFbrReflectorCladding;

public class MeknucFbrTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister("meknuc");

    public static final TileEntityTypeRegistryObject<TileEntityFbrPart> FAST_BREEDER_REACTOR_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_CASING, TileEntityFbrPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityFbrPort> FAST_BREEDER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_PORT, TileEntityFbrPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityFbrLogicAdapter> FAST_BREEDER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_LOGIC_ADAPTER, TileEntityFbrLogicAdapter::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityFbrFuelAssembly> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY, TileEntityFbrFuelAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityFbrControlAssembly> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY, TileEntityFbrControlAssembly::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityFbrReflectorCladding> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING, TileEntityFbrReflectorCladding::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucFbrTileEntityTypes() {
    }
}
