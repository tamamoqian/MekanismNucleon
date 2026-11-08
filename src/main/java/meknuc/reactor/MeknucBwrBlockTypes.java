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
import meknuc.reactor.tile.TileEntityBwrControlAssembly;
import meknuc.reactor.tile.TileEntityBwrFuelAssembly;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrPart;
import meknuc.reactor.tile.TileEntityBwrPort;

public class MeknucBwrBlockTypes {

    public static final BlockTypeTile<TileEntityBwrPart> BOILING_WATER_REACTOR_CASING =
          createExternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_CASING,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_CASING, true);

    public static final BlockTypeTile<TileEntityBwrPort> BOILING_WATER_REACTOR_PORT =
          createPort(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_PORT,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_PORT);

    public static final BlockTypeTile<TileEntityBwrLogicAdapter> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          createLogicAdapter(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER);

    public static final BlockTypeTile<TileEntityBwrFuelAssembly> BOILING_WATER_REACTOR_FUEL_ASSEMBLY =
          createInternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_FUEL_ASSEMBLY,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_FUEL_ASSEMBLY);

    public static final BlockTypeTile<TileEntityBwrControlAssembly> BOILING_WATER_REACTOR_CONTROL_ASSEMBLY =
          createInternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_CONTROL_ASSEMBLY,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_CONTROL_ASSEMBLY);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucBwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType, boolean gui) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        if (gui) {
            builder.with(new AttributeGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR,
                  MeknucBwrLang.GUI_TITLE));
        }
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<TileEntityBwrLogicAdapter> createLogicAdapter(
          MeknucBwrLang description,
          Supplier<TileEntityTypeRegistryObject<TileEntityBwrLogicAdapter>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStateBwrLogicAdapterMode<>(AttributeStateBwrLogicAdapterMode.modeProperty,
              BwrLogicAdapterMode.DISABLED)});
        builder.with(new Attribute[]{Attributes.REDSTONE});
        builder.withGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucBwrLang.GUI_LOGIC_TITLE);
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TileEntityBwrLogicAdapter>)
              ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createPort(
          MeknucBwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStateBwrPortMode(),
              new AttributeGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR, MeknucBwrLang.GUI_TITLE)});
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        builder = (BlockTileBuilder) builder.externalMultiblock();
        return (BlockTypeTile<TILE>) builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          MeknucBwrLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder = (BlockTileBuilder) builder.withSound(MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
    }

    private MeknucBwrBlockTypes() {
    }
}
