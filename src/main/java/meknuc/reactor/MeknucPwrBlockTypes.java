package meknuc.reactor;

import java.util.function.Supplier;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.Attribute.TileAttribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.reactor.tile.TileEntityPwrControlAssembly;
import meknuc.reactor.tile.TileEntityPwrFuelAssembly;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrPart;
import meknuc.reactor.tile.TileEntityPwrPort;

public class MeknucPwrBlockTypes {

    public static final BlockTypeTile<TileEntityPwrPart> PRESSURIZED_WATER_REACTOR_CASING =
          createExternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CASING,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_CASING, true);

    public static final BlockTypeTile<TileEntityPwrPort> PRESSURIZED_WATER_REACTOR_PORT =
          createPort(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_PORT,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_PORT);

    public static final BlockTypeTile<TileEntityPwrLogicAdapter> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          createLogicAdapter(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER);

    public static final BlockTypeTile<TileEntityPwrFuelAssembly> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          createInternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY);

    public static final BlockTypeTile<TileEntityPwrControlAssembly> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          createInternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucPwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType, boolean gui) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        if (gui) {
            builder.with(new AttributeGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR,
                  MeknucPwrLang.GUI_TITLE));
        }
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<TileEntityPwrLogicAdapter> createLogicAdapter(
          MeknucPwrLang description,
          Supplier<TileEntityTypeRegistryObject<TileEntityPwrLogicAdapter>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new TileAttribute[]{new Attributes.AttributeRedstoneEmitter<TileEntityPwrLogicAdapter>(
              TileEntityPwrLogicAdapter::getRedstoneLevel)});
        builder.with(new Attribute[]{Attributes.REDSTONE});
        builder.withGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucPwrLang.GUI_LOGIC_TITLE);
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TileEntityPwrLogicAdapter>)
              ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createPort(
          MeknucPwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStatePwrPortMode(),
              new AttributeGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR, MeknucPwrLang.GUI_TITLE)});
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        builder = (BlockTileBuilder) builder.externalMultiblock();
        return (BlockTypeTile<TILE>) builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          MeknucPwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
    }

    private MeknucPwrBlockTypes() {
    }
}
