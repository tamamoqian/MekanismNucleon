package meknuc.reactor;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum MeknucReactorLang implements ILangEntry {
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_CASING("description", "pressurized_water_reactor_casing"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_PORT("description", "pressurized_water_reactor_port"),
    DESCRIPTION_PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER("description", "pressurized_water_reactor_logic_adapter"),
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
    INVALID_MISSING_CONTROL_ROD("reactor", "invalid_missing_control_rod"),
    INVALID_EXTRA_CONTROL_ROD("reactor", "invalid_extra_control_rod"),
    INVALID_BAD_FUEL_ASSEMBLY("reactor", "invalid_bad_fuel_assembly"),
    INVALID_MALFORMED_FUEL_ASSEMBLY("reactor", "invalid_malformed_fuel_assembly"),
    INVALID_BAD_CONTROL_ROD("reactor", "invalid_bad_control_rod"),
    INVALID_RIM_REQUIRES_CASING("reactor", "invalid_rim_requires_casing"),

    GUI_TITLE("reactor", "title"),
    GUI_ACTIVATE("reactor", "activate"),
    GUI_SCRAM("reactor", "scram"),
    GUI_STATUS("reactor", "status"),
    GUI_STATE_ACTIVE("reactor", "state_active"),
    GUI_STATE_STOPPED("reactor", "state_stopped"),
    GUI_STATE_MELTDOWN("reactor", "state_meltdown"),
    GUI_TEMPERATURE("reactor", "temperature"),
    GUI_BURN_TIME("reactor", "burn_time"),
    GUI_DAMAGE("reactor", "damage"),
    GUI_DAMAGE_MELTDOWN("reactor", "damage_meltdown"),
    GUI_FUEL("reactor", "fuel"),
    GUI_IN_CORE("reactor", "in_core"),
    GUI_FUEL_HINT("reactor", "fuel_hint"),
    GUI_FUEL_TYPE("reactor", "fuel_type"),
    GUI_WASTE("reactor", "waste"),
    GUI_TEMPERATURE_BAR("reactor", "temperature_bar"),
    GUI_CONTROL_ROD("reactor", "control_rod"),
    GUI_HEAT_GRAPH("reactor", "heat_graph"),
    GUI_COOLANT_TANK("reactor", "coolant_tank"),
    GUI_HEATED_COOLANT_TANK("reactor", "heated_coolant_tank"),
    GUI_FUEL_CACHE("reactor", "fuel_cache"),
    GUI_WASTE_CACHE("reactor", "waste_cache"),

    GUI_MAIN_TAB("reactor", "main_tab"),
    GUI_STATS_TAB("reactor", "stats_tab"),
    GUI_STATS_TITLE("reactor", "stats_title"),
    GUI_HEAT_STATISTICS("reactor", "heat_statistics"),
    GUI_HEAT_CAPACITY("reactor", "heat_capacity"),
    GUI_MAX_EXCHANGE("reactor", "max_exchange"),
    GUI_COOLANT_CAPACITY("reactor", "coolant_capacity"),
    GUI_FUEL_STATISTICS("reactor", "fuel_statistics"),
    GUI_FUEL_ROD_COUNT("reactor", "fuel_rod_count"),
    GUI_BURN_TIME_BATCH("reactor", "burn_time_batch"),
    GUI_CONTROL_ROD_INSERTION("reactor", "control_rod_insertion"),
    GUI_IN_CORE_COUNT("reactor", "in_core_count"),
    GUI_OPERATING_STATISTICS("reactor", "operating_statistics"),
    GUI_CORE_DAMAGE("reactor", "core_damage"),
    GUI_CORE_DAMAGE_MELTDOWN("reactor", "core_damage_meltdown"),
    GUI_COOLANT_USAGE("reactor", "coolant_usage"),
    GUI_STEAM_PRODUCTION("reactor", "steam_production"),

    LOGIC_DISABLED("reactor", "logic.disabled"),
    LOGIC_ACTIVATION("reactor", "logic.activation"),
    LOGIC_CONTROL_ROD("reactor", "logic.control_rod"),
    LOGIC_HIGH_TEMPERATURE("reactor", "logic.high_temperature"),
    LOGIC_CRITICAL_WASTE_LEVEL("reactor", "logic.critical_waste_level"),
    LOGIC_DAMAGED("reactor", "logic.damaged"),
    LOGIC_OUTPUTTING("reactor", "logic.outputting"),
    LOGIC_POWERED("reactor", "logic.powered"),

    DESCRIPTION_LOGIC_DISABLED("description", "reactor.logic.disabled"),
    DESCRIPTION_LOGIC_ACTIVATION("description", "reactor.logic.activation"),
    DESCRIPTION_LOGIC_CONTROL_ROD("description", "reactor.logic.control_rod"),
    DESCRIPTION_LOGIC_HIGH_TEMPERATURE("description", "reactor.logic.high_temperature"),
    DESCRIPTION_LOGIC_CRITICAL_WASTE_LEVEL("description", "reactor.logic.critical_waste_level"),
    DESCRIPTION_LOGIC_DAMAGED("description", "reactor.logic.damaged"),

    GUI_LOGIC_TITLE("reactor", "logic.title"),
    GUI_LOGIC_MODE("reactor", "logic.redstone_mode"),

    JEI_REACTOR_FUEL("jei", "reactor_fuel"),
    JEI_REACTOR_FUEL_BURN_TIME("jei", "reactor_fuel.burn_time"),
    JEI_REACTOR_FUEL_BATCH("jei", "reactor_fuel.batch"),

    JEI_REACTOR_COOLANT("jei", "reactor_coolant");

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
