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

public class MeknucBlockThoriumMoltenSaltReactorPort extends MeknucBlockReactorPortBase {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.BOILING_COOLANT_FISSION_WASTE_OUTPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.THORIUM_MOLTEN_SALT_REACTOR_PORT, MeknucBlockReactorLang.THORIUM_MOLTEN_SALT_REACTOR_PORT, MODE_ATTRIBUTE);

    public MeknucBlockThoriumMoltenSaltReactorPort(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        BOILING_COOLANT_FISSION_WASTE_OUTPUT("boiling_coolant_fission_waste_output", MeknucBlockReactorLang.PORT_MODE_THORIUM_MOLTEN_SALT_REACTOR_BOILING_COOLANT_FISSION_WASTE_OUTPUT, EnumColor.BROWN),
        MOLTEN_COOLANT_FISSION_FUEL_COMBINED_INPUT("molten_coolant_fission_fuel_combined_input", MeknucBlockReactorLang.PORT_MODE_THORIUM_MOLTEN_SALT_REACTOR_MOLTEN_COOLANT_FISSION_FUEL_COMBINED_INPUT, EnumColor.BRIGHT_GREEN),
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
