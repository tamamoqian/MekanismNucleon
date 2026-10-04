package meknuc.blocks;

import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import meknuc.blockentities.MeknucTileEntityTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MeknucBlockHighTemperatureGraphiteGasCooledReactorPort extends MeknucBlockReactorPortBase {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.COOLANT_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT, MeknucBlockReactorLang.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_PORT, MODE_ATTRIBUTE);

    public MeknucBlockHighTemperatureGraphiteGasCooledReactorPort(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        COOLANT_INPUT("coolant_input", MeknucBlockReactorLang.PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_COOLANT_INPUT, EnumColor.AQUA),
        COOLANT_OUTPUT("coolant_output", MeknucBlockReactorLang.PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_COOLANT_OUTPUT, EnumColor.DARK_AQUA),
        FUEL_PARTICLE_INPUT("fuel_particle_input", MeknucBlockReactorLang.PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_FUEL_PARTICLE_INPUT, EnumColor.BRIGHT_GREEN),
        WASTE_PARTICLE_OUTPUT("waste_particle_output", MeknucBlockReactorLang.PORT_MODE_HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_WASTE_PARTICLE_OUTPUT, EnumColor.BROWN),
        ;

        private final String name;
        private final ILangEntry langEntry;
        private final EnumColor color;

        Mode(String name, ILangEntry langEntry, EnumColor color) {
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
}
