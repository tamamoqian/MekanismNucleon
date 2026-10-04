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

public class MeknucBlockParticleAcceleratorPort extends MeknucBlockReactorPortBase {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.BEAM_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.PARTICLE_ACCELERATOR_PORT, MeknucBlockReactorLang.PARTICLE_ACCELERATOR_PORT, MODE_ATTRIBUTE);

    public MeknucBlockParticleAcceleratorPort(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        BEAM_INPUT("beam_input", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_BEAM_INPUT, EnumColor.PURPLE),
        BEAM_OUTPUT("beam_output", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_BEAM_OUTPUT, EnumColor.INDIGO),
        ENERGY("energy", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_ENERGY, EnumColor.RED),
        TARGET_INPUT("target_input", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_TARGET_INPUT, EnumColor.PINK),
        TARGET_OUTPUT("target_output", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_TARGET_OUTPUT, EnumColor.BRIGHT_PINK),
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
