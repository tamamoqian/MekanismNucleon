package meknuc.reactor;

import java.util.List;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class AttributeStatePwrPortMode implements AttributeState {

    public static final EnumProperty<ReactorPortMode> modeProperty = EnumProperty.create("mode", ReactorPortMode.class);

    @Override
    public BlockState copyStateData(BlockState oldState, BlockState newState) {
        if (Attribute.has(newState, AttributeStatePwrPortMode.class)) {
            newState = newState.setValue(modeProperty, oldState.getValue(modeProperty));
        }
        return newState;
    }

    @Override
    public BlockState getDefaultState(@NotNull BlockState state) {
        return state.setValue(modeProperty, ReactorPortMode.COOLANT_INPUT);
    }

    @Override
    public void fillBlockStateContainer(Block block, List<Property<?>> properties) {
        properties.add(modeProperty);
    }

    public enum ReactorPortMode implements StringRepresentable, IHasEnumNameTextComponent {
        COOLANT_INPUT("input_coolant", MeknucPwrLang.PORT_MODE_COOLANT_INPUT, EnumColor.AQUA),
        FUEL_INPUT("input_fuel", MeknucPwrLang.PORT_MODE_FUEL_INPUT, EnumColor.BRIGHT_GREEN),
        COOLANT_OUTPUT("output_coolant", MeknucPwrLang.PORT_MODE_COOLANT_OUTPUT, EnumColor.DARK_AQUA),
        WASTE_OUTPUT("output_waste", MeknucPwrLang.PORT_MODE_WASTE_OUTPUT, EnumColor.BROWN);

        private final String name;
        private final ILangEntry langEntry;
        private final EnumColor color;

        ReactorPortMode(String name, ILangEntry langEntry, EnumColor color) {
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

        public ReactorPortMode getNext() {
            ReactorPortMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }
}
