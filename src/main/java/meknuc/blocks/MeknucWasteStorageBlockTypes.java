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

public class MeknucWasteStorageBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.RADIOACTIVE_WASTE_INPUT);

    public static final BlockTypeTile<MeknucBlockEntityReactorPart> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          createExternal(MeknucBlockReactorLang.RADIATION_RESISTANT_WASTE_STORAGE_CASING,
                () -> MeknucTileEntityTypes.RADIATION_RESISTANT_WASTE_STORAGE_CASING);

    public static final BlockTypeTile<MeknucBlockEntityReactorPort> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          createPort(MeknucBlockReactorLang.RADIATION_RESISTANT_WASTE_STORAGE_PORT,
                () -> MeknucTileEntityTypes.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MODE_ATTRIBUTE);

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

    private MeknucWasteStorageBlockTypes() {
    }
}
