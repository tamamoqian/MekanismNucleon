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
import meknuc.blockentities.MeknucTileEntityTypes;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolContainerTypes;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolLogicAdapter;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolPort;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MeknucSpentFuelPoolBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.COOLANT_INPUT);

    public static final BlockTypeTile<TileEntitySpentFuelPoolCasing> SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING =
          createExternal(MeknucBlockReactorLang.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING,
                () -> MeknucTileEntityTypes.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING);

    public static final BlockTypeTile<TileEntitySpentFuelPoolPort> SPENT_FUEL_POOL_PORT =
          createPort(MeknucBlockReactorLang.SPENT_FUEL_POOL_PORT, () -> MeknucTileEntityTypes.SPENT_FUEL_POOL_PORT, MODE_ATTRIBUTE);

    public static final BlockTypeTile<TileEntitySpentFuelPoolLogicAdapter> SPENT_FUEL_POOL_LOGIC_ADAPTER =
          createLogicAdapter(MeknucBlockReactorLang.SPENT_FUEL_POOL_LOGIC_ADAPTER,
                () -> MeknucTileEntityTypes.SPENT_FUEL_POOL_LOGIC_ADAPTER);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucBlockReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.withGui(() -> MeknucSpentFuelPoolContainerTypes.SPENT_FUEL_POOL, MeknucBlockReactorLang.SPENT_FUEL_POOL_GUI_TITLE);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<TileEntitySpentFuelPoolPort> createPort(
          MeknucBlockReactorLang description,
          Supplier<TileEntityTypeRegistryObject<TileEntitySpentFuelPoolPort>> tileType,
          AttributeStateReactorPortMode<?> mode) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{mode});
        return (BlockTypeTile<TileEntitySpentFuelPoolPort>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<TileEntitySpentFuelPoolLogicAdapter> createLogicAdapter(
          MeknucBlockReactorLang description,
          Supplier<TileEntityTypeRegistryObject<TileEntitySpentFuelPoolLogicAdapter>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStateLogicAdapterMode<>(MeknucLogicAdapterMode.MODE, MeknucLogicAdapterMode.DISABLED)});
        return (BlockTypeTile<TileEntitySpentFuelPoolLogicAdapter>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        COOLANT_INPUT("coolant_input", MeknucBlockReactorLang.PORT_MODE_SPENT_FUEL_POOL_COOLANT_INPUT, EnumColor.AQUA),
        COOLANT_OUTPUT("coolant_output", MeknucBlockReactorLang.PORT_MODE_SPENT_FUEL_POOL_COOLANT_OUTPUT, EnumColor.WHITE),
        WASTE_INPUT("waste_input", MeknucBlockReactorLang.PORT_MODE_SPENT_FUEL_POOL_WASTE_INPUT, EnumColor.BROWN),
        WASTE_OUTPUT("waste_output", MeknucBlockReactorLang.PORT_MODE_SPENT_FUEL_POOL_WASTE_OUTPUT, EnumColor.BROWN),
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

    private MeknucSpentFuelPoolBlockTypes() {
    }
}
