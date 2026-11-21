package meknuc.blockentities;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.MeknucFbrBlocks;
import meknuc.blocks.MeknucHtgrBlocks;
import meknuc.blocks.MeknucHwrBlocks;
import meknuc.blocks.MeknucMoltenSaltReactorBlocks;
import meknuc.blocks.MeknucParticleAcceleratorBlocks;
import meknuc.blocks.MeknucSpentFuelPoolBlocks;
import meknuc.blocks.MeknucWasteStorageBlocks;
import meknuc.init.MeknucBlock;
import meknuc.Meknuc;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import meknuc.particle.tile.TileEntityParticleAcceleratorPart;
import meknuc.particle.tile.TileEntityParticleAcceleratorPort;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolLogicAdapter;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolPort;
import meknuc.wastestorage.tile.TileEntityWasteStorageCasing;
import meknuc.wastestorage.tile.TileEntityWasteStoragePort;

public class MeknucTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityRTG> RADIOISOTOPE_THERMOELECTRIC_GENERATOR =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBlock.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, MeknucBlockEntityRTG::new)
                .serverTicker((level, pos, state, be) -> TileEntityMekanism.tickServer(level, pos, state, be))
                .build();

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

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorController> PARTICLE_ACCELERATOR_CONTROLLER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_CONTROLLER, TileEntityParticleAcceleratorController::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_SUPPORT_FRAME, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATION_COIL =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATION_COIL, TileEntityParticleAcceleratorPart::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPort> PARTICLE_ACCELERATOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT, TileEntityParticleAcceleratorPort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityWasteStorageCasing> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_CASING, TileEntityWasteStorageCasing::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityWasteStoragePort> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_PORT, TileEntityWasteStoragePort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucTileEntityTypes() {
    }
}
