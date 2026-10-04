package meknuc.blocks;

import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import meknuc.reactor.MeknucPwrLang;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MeknucLogicAdapterMode implements StringRepresentable, IHasEnumNameTextComponent {
    DISABLED("disabled", MeknucPwrLang.LOGIC_DISABLED, EnumColor.DARK_GRAY),
    ACTIVATION("activation", MeknucPwrLang.LOGIC_ACTIVATION, EnumColor.AQUA),
    CONTROL_ROD("control_rod", MeknucPwrLang.LOGIC_CONTROL_ROD, EnumColor.BRIGHT_GREEN),
    HIGH_TEMPERATURE("high_temperature", MeknucPwrLang.LOGIC_HIGH_TEMPERATURE, EnumColor.RED),
    CRITICAL_WASTE_LEVEL("critical_waste_level", MeknucPwrLang.LOGIC_CRITICAL_WASTE_LEVEL, EnumColor.RED),
    DAMAGED("damaged", MeknucPwrLang.LOGIC_DAMAGED, EnumColor.RED),
    ;

    private final String name;
    private final ILangEntry langEntry;
    private final EnumColor color;

    MeknucLogicAdapterMode(String name, ILangEntry langEntry, EnumColor color) {
        this.name = name;
        this.langEntry = langEntry;
        this.color = color;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component getTextComponent() {
        return langEntry.translateColored(color);
    }
}
