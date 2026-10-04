package meknuc.blocks;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum MeknucBlockReactorLang implements ILangEntry {
    BOILING_WATER_REACTOR_CASING("description", "boiling_water_reactor_casing"),
    BOILING_WATER_REACTOR_CONTROL_ASSEMBLY("description", "boiling_water_reactor_control_assembly"),
    BOILING_WATER_REACTOR_FUEL_ASSEMBLY("description", "boiling_water_reactor_fuel_assembly"),
    BOILING_WATER_REACTOR_PORT("description", "boiling_water_reactor_port"),
    BOILING_WATER_REACTOR_LOGIC_ADAPTER("description", "boiling_water_reactor_logic_adapter"),
    FAST_BREEDER_REACTOR_CASING("description", "fast_breeder_reactor_casing"),
    FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING("description", "fast_breeder_reactor_neutron_reflector_cladding"),
    FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY("description", "fast_breeder_reactor_radiation_resistant_control_assembly"),
    FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY("description", "fast_breeder_reactor_radiation_resistant_fuel_assembly"),
    FAST_BREEDER_REACTOR_PORT("description", "fast_breeder_reactor_port"),
    HEAVY_WATER_REACTOR_CASING("description", "heavy_water_reactor_casing"),
    HEAVY_WATER_REACTOR_CONTROL_ASSEMBLY("description", "heavy_water_reactor_control_assembly"),
    HEAVY_WATER_REACTOR_FUEL_ASSEMBLY("description", "heavy_water_reactor_fuel_assembly"),
    HEAVY_WATER_REACTOR_PORT("description", "heavy_water_reactor_port"),
    HEAVY_WATER_REACTOR_LOGIC_ADAPTER("description", "heavy_water_reactor_logic_adapter"),
    HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_CASING("description", "high_temperature_graphite_gas_cooled_reactor_casing"),
    HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_CONTROL_ASSEMBLY("description", "high_temperature_graphite_gas_cooled_reactor_fuel_particle_control_assembly"),
    HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PEBBLE_BED("description", "high_temperature_graphite_gas_cooled_reactor_fuel_pebble_bed"),
    HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT("description", "high_temperature_graphite_gas_cooled_reactor_port"),
    HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER("description", "high_temperature_graphite_gas_cooled_reactor_logic_adapter"),
    THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING("description", "thorium_molten_salt_reactor_corrosion_resistant_casing"),
    THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY("description", "thorium_molten_salt_reactor_corrosion_resistant_control_assembly"),
    THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY("description", "thorium_molten_salt_reactor_corrosion_resistant_fuel_assembly"),
    THORIUM_MOLTEN_SALT_REACTOR_PORT("description", "thorium_molten_salt_reactor_port"),
    PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING("description", "particle_accelerator_radiation_resistant_casing"),
    PARTICLE_ACCELERATOR_SUPPORT_FRAME("description", "particle_accelerator_support_frame"),
    PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME("description", "particle_accelerator_target_mounting_frame"),
    PARTICLE_ACCELERATION_COIL("description", "particle_acceleration_coil"),
    PARTICLE_ACCELERATOR_PORT("description", "particle_accelerator_port"),
    SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING("description", "spent_fuel_pool_radiation_resistant_casing"),
    SPENT_FUEL_POOL_PORT("description", "spent_fuel_pool_port"),
    SPENT_FUEL_POOL_LOGIC_ADAPTER("description", "spent_fuel_pool_logic_adapter"),
    RADIATION_RESISTANT_WASTE_STORAGE_CASING("description", "radiation_resistant_waste_storage_casing"),
    PORT_MODE_BOILING_WATER_REACTOR_COOLANT_INPUT("port_mode", "boiling_water_reactor_coolant_input"),
    PORT_MODE_BOILING_WATER_REACTOR_COOLANT_OUTPUT("port_mode", "boiling_water_reactor_coolant_output"),
    PORT_MODE_BOILING_WATER_REACTOR_FUEL_INPUT("port_mode", "boiling_water_reactor_fuel_input"),
    PORT_MODE_BOILING_WATER_REACTOR_WASTE_OUTPUT("port_mode", "boiling_water_reactor_waste_output"),
    PORT_MODE_HEAVY_WATER_REACTOR_COOLANT_INPUT("port_mode", "heavy_water_reactor_coolant_input"),
    PORT_MODE_HEAVY_WATER_REACTOR_COOLANT_OUTPUT("port_mode", "heavy_water_reactor_coolant_output"),
    PORT_MODE_HEAVY_WATER_REACTOR_FUEL_INPUT("port_mode", "heavy_water_reactor_fuel_input"),
    PORT_MODE_HEAVY_WATER_REACTOR_WASTE_OUTPUT("port_mode", "heavy_water_reactor_waste_output"),
    PORT_MODE_SPENT_FUEL_POOL_COOLANT_INPUT("port_mode", "spent_fuel_pool_coolant_input"),
    PORT_MODE_SPENT_FUEL_POOL_COOLANT_OUTPUT("port_mode", "spent_fuel_pool_coolant_output"),
    PORT_MODE_SPENT_FUEL_POOL_WASTE_INPUT("port_mode", "spent_fuel_pool_waste_input"),
    PORT_MODE_SPENT_FUEL_POOL_WASTE_OUTPUT("port_mode", "spent_fuel_pool_waste_output"),
    PORT_MODE_FAST_BREEDER_REACTOR_COOLANT_INPUT("port_mode", "fast_breeder_reactor_coolant_input"),
    PORT_MODE_FAST_BREEDER_REACTOR_COOLANT_OUTPUT("port_mode", "fast_breeder_reactor_coolant_output"),
    PORT_MODE_FAST_BREEDER_REACTOR_FUEL_INPUT("port_mode", "fast_breeder_reactor_fuel_input"),
    PORT_MODE_FAST_BREEDER_REACTOR_BREEDER_FUEL_OUTPUT("port_mode", "fast_breeder_reactor_breeder_fuel_output"),
    PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_COOLANT_INPUT("port_mode", "high_temperature_graphite_gas_cooled_reactor_coolant_input"),
    PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_COOLANT_OUTPUT("port_mode", "high_temperature_graphite_gas_cooled_reactor_coolant_output"),
    PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_INPUT("port_mode", "high_temperature_graphite_gas_cooled_reactor_fuel_particle_input"),
    PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_WASTE_PARTICLE_OUTPUT("port_mode", "high_temperature_graphite_gas_cooled_reactor_waste_particle_output"),
    PORT_MODE_PARTICLE_ACCELERATOR_BEAM_INPUT("port_mode", "particle_accelerator_beam_input"),
    PORT_MODE_PARTICLE_ACCELERATOR_BEAM_OUTPUT("port_mode", "particle_accelerator_beam_output"),
    PORT_MODE_PARTICLE_ACCELERATOR_ENERGY("port_mode", "particle_accelerator_energy"),
    PORT_MODE_PARTICLE_ACCELERATOR_TARGET_INPUT("port_mode", "particle_accelerator_target_input"),
    PORT_MODE_PARTICLE_ACCELERATOR_TARGET_OUTPUT("port_mode", "particle_accelerator_target_output"),
    PORT_MODE_THORIUM_MOLTEN_SALT_REACTOR_BOILING_COOLANT_FISSION_WASTE_OUTPUT("port_mode", "thorium_molten_salt_reactor_boiling_coolant_fission_waste_output"),
    PORT_MODE_THORIUM_MOLTEN_SALT_REACTOR_MOLTEN_COOLANT_FISSION_FUEL_COMBINED_INPUT("port_mode", "thorium_molten_salt_reactor_molten_coolant_fission_fuel_combined_input"),
    PORT_MODE_RADIATION_RESISTANT_WASTE_STORAGE_RADIOACTIVE_WASTE_INPUT("port_mode", "radiation_resistant_waste_storage_radioactive_waste_input"),
    PORT_MODE_RADIATION_RESISTANT_WASTE_STORAGE_DECAY_PRODUCT_OUTPUT("port_mode", "radiation_resistant_waste_storage_decay_product_output"),
    RADIATION_RESISTANT_WASTE_STORAGE_PORT("description", "radiation_resistant_waste_storage_port");

    private final String key;

    MeknucBlockReactorLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath("meknuc", path)));
    }

    MeknucBlockReactorLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
