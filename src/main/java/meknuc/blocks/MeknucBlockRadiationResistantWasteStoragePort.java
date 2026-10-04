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

public class MeknucBlockRadiationResistantWasteStoragePort extends MeknucBlockReactorPortBase {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.RADIOACTIVE_WASTE_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MeknucBlockReactorLang.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MODE_ATTRIBUTE);

    public MeknucBlockRadiationResistantWasteStoragePort(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        RADIOACTIVE_WASTE_INPUT("radioactive_waste_input", MeknucBlockReactorLang.PORT_MODE_RADIATION_RESISTANT_WASTE_STORAGE_RADIOACTIVE_WASTE_INPUT, EnumColor.BROWN),
        DECAY_PRODUCT_OUTPUT("decay_product_output", MeknucBlockReactorLang.PORT_MODE_RADIATION_RESISTANT_WASTE_STORAGE_DECAY_PRODUCT_OUTPUT, EnumColor.DARK_GRAY),
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
