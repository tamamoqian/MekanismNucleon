package meknuc.reactor.fbr;

import java.util.function.Supplier;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import meknuc.blocks.AttributeStateLogicAdapterMode;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.blocks.MeknucBlockReactorLang;
import meknuc.blocks.MeknucLogicAdapterMode;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MeknucFbrBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.COOLANT_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_CASING =
          createExternal(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_CASING, () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_CASING);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING =
          createInternal(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING,
                () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY =
          createInternal(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY,
                () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY =
          createInternal(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY,
                () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> FAST_BREEDER_REACTOR_PORT =
          createPort(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_PORT, () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_PORT, MODE_ATTRIBUTE);

    public static final BlockTypeTile<MeknucBlockEntityLogicAdapter> FAST_BREEDER_REACTOR_LOGIC_ADAPTER =
          createLogicAdapter(MeknucBlockReactorLang.FAST_BREEDER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucFbrTileEntityTypes.FAST_BREEDER_REACTOR_LOGIC_ADAPTER);

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
    private static BlockTypeTile<MeknucBlockEntityLogicAdapter> createLogicAdapter(
          MeknucBlockReactorLang description,
          Supplier<TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStateLogicAdapterMode<>(MeknucLogicAdapterMode.MODE, MeknucLogicAdapterMode.DISABLED)});
        return (BlockTypeTile<MeknucBlockEntityLogicAdapter>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          MeknucBlockReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
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

    private MeknucFbrBlockTypes() {
    }
}
