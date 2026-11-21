package meknuc.reactor;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum MeknucFbrLang implements ILangEntry {
    INVALID_SIZE("reactor", "fbr.invalid_size"),
    INVALID_HEIGHT("reactor", "fbr.invalid_height"),
    INVALID_STEP("reactor", "fbr.invalid_step"),
    INVALID_MISSING_FUEL("reactor", "fbr.invalid_missing_fuel"),
    INVALID_MISSING_CONTROL_ROD("reactor", "fbr.invalid_missing_control_rod"),
    INVALID_EXTRA_CONTROL_ROD("reactor", "fbr.invalid_extra_control_rod"),
    INVALID_BAD_CONTROL_ROD("reactor", "fbr.invalid_bad_control_rod"),
    INVALID_MALFORMED_FUEL_ASSEMBLY("reactor", "fbr.invalid_malformed_fuel_assembly"),
    INVALID_FUEL_HEIGHT("reactor", "fbr.invalid_fuel_height"),
    INVALID_SHAPE("reactor", "fbr.invalid_shape"),
    INVALID_REFLECTOR("reactor", "fbr.invalid_reflector"),
    INVALID_FUEL_SPACING("reactor", "fbr.invalid_fuel_spacing"),
    GUI_TITLE("gui", "fbr.title");

    private final String key;

    MeknucFbrLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath("meknuc", path)));
    }

    MeknucFbrLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
