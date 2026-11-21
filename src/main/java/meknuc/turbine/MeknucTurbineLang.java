package meknuc.turbine;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

/**
 * 高压蒸汽涡轮各方块的描述键（{@code description.meknuc.<id>}）。
 */
public enum MeknucTurbineLang implements ILangEntry {
    HIGH_PRESSURE_STEAM_TURBINE_CASING("description", "high_pressure_steam_turbine_casing"),
    HIGH_PRESSURE_STEAM_TURBINE_PORT("description", "high_pressure_steam_turbine_port"),
    HIGH_PRESSURE_STEAM_TURBINE_VENT("description", "high_pressure_steam_turbine_vent"),
    HIGH_PRESSURE_STEAM_CONDENSER("description", "high_pressure_steam_condenser"),
    HIGH_PRESSURE_TURBINE_GUI("gui", "high_pressure_industrial_turbine.title");

    private final String key;

    MeknucTurbineLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath("meknuc", path)));
    }

    MeknucTurbineLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
