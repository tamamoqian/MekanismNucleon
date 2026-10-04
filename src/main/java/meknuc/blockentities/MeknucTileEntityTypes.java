package meknuc.blockentities;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.MeknucBlockBase;
import meknuc.blocks.MeknucBwrBlocks;
import meknuc.blocks.MeknucFbrBlocks;
import meknuc.blocks.MeknucHtgrBlocks;
import meknuc.blocks.MeknucHwrBlocks;
import meknuc.blocks.MeknucMoltenSaltReactorBlocks;
import meknuc.blocks.MeknucParticleAcceleratorBlocks;
import meknuc.blocks.MeknucSpentFuelPoolBlocks;
import meknuc.blocks.MeknucWasteStorageBlocks;
import meknuc.meknuc;

public class MeknucTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(meknuc.MODID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityRTG> RADIOISOTOPE_THERMOELECTRIC_GENERATOR =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBlockBase.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, MeknucBlockEntityRTG::new)
                .serverTicker((level, pos, state, be) -> TileEntityMekanism.tickServer(level, pos, state, be))
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> BOILING_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBwrBlocks.BOILING_WATER_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> HEAVY_WATER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> HEAVY_WATER_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHwrBlocks.HEAVY_WATER_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> SPENT_FUEL_POOL_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> FAST_BREEDER_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucFbrBlocks.FAST_BREEDER_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucHtgrBlocks.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> PARTICLE_ACCELERATOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucTileEntityTypes() {
    }
}
