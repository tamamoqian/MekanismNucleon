package meknuc.reactor;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum MeknucReactorLang implements ILangEntry {
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_CASING("description", "pressurized_water_reactor_casing"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_PORT("description", "pressurized_water_reactor_port"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_SIGNAL_PORT("description", "pressurized_water_reactor_signal_port"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY("description", "pressurized_water_reactor_fuel_assembly"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY("description", "pressurized_water_reactor_control_assembly"),

    PORT_MODE_COOLANT_INPUT("port_mode", "coolant_input"),
    PORT_MODE_FUEL_INPUT("port_mode", "fuel_input"),
    PORT_MODE_COOLANT_OUTPUT("port_mode", "coolant_output"),
    PORT_MODE_WASTE_OUTPUT("port_mode", "waste_output"),

    INVALID_NOT_CYLINDER("reactor", "invalid_not_cylinder"),
    INVALID_HEIGHT("reactor", "invalid_height"),
    INVALID_SHELL_MISSING("reactor", "invalid_shell_missing"),
    INVALID_SHELL_EXTRA("reactor", "invalid_shell_extra"),
    INVALID_INNER("reactor", "invalid_inner"),
    INVALID_MISSING_FUEL("reactor", "invalid_missing_fuel"),
    INVALID_MISSING_CONTROL("reactor", "invalid_missing_control"),
    INVALID_MALFORMED_COLUMN("reactor", "invalid_malformed_column"),
    INVALID_BAD_STAGGER("reactor", "invalid_bad_stagger"),
    INVALID_GAP_IN_COLUMN("reactor", "invalid_gap_in_column"),
    INVALID_RIM_REQUIRES_CASING("reactor", "invalid_rim_requires_casing"),

    GUI_ACTIVATE("reactor", "activate"),
    GUI_SCRAM("reactor", "scram"),
    GUI_STATUS("reactor", "status"),
    GUI_STATE_ACTIVE("reactor", "state_active"),
    GUI_STATE_STOPPED("reactor", "state_stopped"),
    GUI_TEMPERATURE("reactor", "temperature"),
    GUI_BURN_TIME("reactor", "burn_time"),
    GUI_FUEL("reactor", "fuel"),
    GUI_FUEL_TYPE("reactor", "fuel_type"),
    GUI_WASTE("reactor", "waste"),
    GUI_TEMPERATURE_BAR("reactor", "temperature_bar"),
    GUI_CONTROL_ROD("reactor", "control_rod"),
    GUI_HEAT_GRAPH("reactor", "heat_graph"),
    GUI_COOLANT_TANK("reactor", "coolant_tank"),
    GUI_HEATED_COOLANT_TANK("reactor", "heated_coolant_tank"),
    GUI_FUEL_CACHE("reactor", "fuel_cache"),
    GUI_WASTE_CACHE("reactor", "waste_cache");

    private final String key;

    MeknucReactorLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath("meknuc", path)));
    }

    MeknucReactorLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
