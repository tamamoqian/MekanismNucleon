package meknuc.blockentities;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.MeknucBlockBase;
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

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> SPENT_FUEL_POOL_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucSpentFuelPoolBlocks.SPENT_FUEL_POOL_LOGIC_ADAPTER, MeknucBlockEntityLogicAdapter::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

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

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_SUPPORT_FRAME, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATION_COIL =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATION_COIL, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> PARTICLE_ACCELERATOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucTileEntityTypes() {
    }
}
