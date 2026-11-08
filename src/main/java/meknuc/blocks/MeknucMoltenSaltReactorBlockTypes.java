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

public class MeknucMoltenSaltReactorBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.BOILING_COOLANT_FISSION_WASTE_OUTPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING =
          createExternal(MeknucBlockReactorLang.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING,
                () -> MeknucTileEntityTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY =
          createInternal(MeknucBlockReactorLang.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY,
                () -> MeknucTileEntityTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY =
          createInternal(MeknucBlockReactorLang.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY,
                () -> MeknucTileEntityTypes.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          createPort(MeknucBlockReactorLang.THORIUM_MOLTEN_SALT_REACTOR_PORT,
                () -> MeknucTileEntityTypes.THORIUM_MOLTEN_SALT_REACTOR_PORT, MODE_ATTRIBUTE);

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          MeknucBlockReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
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

    private MeknucMoltenSaltReactorBlockTypes() {
    }
}
