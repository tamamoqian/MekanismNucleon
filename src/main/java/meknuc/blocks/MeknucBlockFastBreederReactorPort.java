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

public class MeknucBlockFastBreederReactorPort extends MeknucBlockReactorPortBase {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.COOLANT_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.FAST_BREEDER_REACTOR_PORT, MeknucBlockReactorLang.FAST_BREEDER_REACTOR_PORT, MODE_ATTRIBUTE);

    public MeknucBlockFastBreederReactorPort(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        COOLANT_INPUT("coolant_input", MeknucBlockReactorLang.PORT_MODE_FAST_BREEDER_REACTOR_COOLANT_INPUT, EnumColor.AQUA),
        COOLANT_OUTPUT("coolant_output", MeknucBlockReactorLang.PORT_MODE_FAST_BREEDER_REACTOR_COOLANT_OUTPUT, EnumColor.DARK_AQUA),
        FUEL_INPUT("fuel_input", MeknucBlockReactorLang.PORT_MODE_FAST_BREEDER_REACTOR_FUEL_INPUT, EnumColor.BRIGHT_GREEN),
        BREEDER_FUEL_OUTPUT("breeder_fuel_output", MeknucBlockReactorLang.PORT_MODE_FAST_BREEDER_REACTOR_BREEDER_FUEL_OUTPUT, EnumColor.ORANGE),
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
