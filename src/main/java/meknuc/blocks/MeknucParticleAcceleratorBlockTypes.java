package meknuc.blocks;

import java.util.function.Supplier;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import meknuc.blockentities.MeknucTileEntityTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MeknucParticleAcceleratorBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.BEAM_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING,
                () -> MeknucTileEntityTypes.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_SUPPORT_FRAME,
                () -> MeknucTileEntityTypes.PARTICLE_ACCELERATOR_SUPPORT_FRAME);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME,
                () -> MeknucTileEntityTypes.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> PARTICLE_ACCELERATION_COIL =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATION_COIL,
                () -> MeknucTileEntityTypes.PARTICLE_ACCELERATION_COIL);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> PARTICLE_ACCELERATOR_PORT =
          createPort(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_PORT,
                () -> MeknucTileEntityTypes.PARTICLE_ACCELERATOR_PORT, MODE_ATTRIBUTE);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucBlockReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<MeknucBlockEntityReactorPort> createPort(
          MeknucBlockReactorLang description,
          Supplier<TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort>> tileType,
          AttributeStateReactorPortMode<?> mode) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{mode});
        return (BlockTypeTile<MeknucBlockEntityReactorPort>) ((BlockTileBuilder) builder.externalMultiblock()).build();
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

    private MeknucParticleAcceleratorBlockTypes() {
    }
}
