package meknuc.reactor;

import java.util.function.Supplier;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorControlAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorFuelAssembly;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorLogicAdapter;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPort;

public class MeknucReactorBlockTypes {

    public static final BlockTypeTile<TileEntityPressurizedWaterReactorPart> PRESSURIZED_WATER_REACTOR_CASING =
          createExternal(MeknucReactorLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CASING,
                () -> MeknucReactorTileEntityTypes.PRESSURIZED_WATER_REACTOR_CASING, true);

    public static final BlockTypeTile<TileEntityPressurizedWaterReactorPort> PRESSURIZED_WATER_REACTOR_PORT =
          createPort(MeknucReactorLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_PORT,
                () -> MeknucReactorTileEntityTypes.PRESSURIZED_WATER_REACTOR_PORT);

    public static final BlockTypeTile<TileEntityPressurizedWaterReactorLogicAdapter> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          createExternal(MeknucReactorLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucReactorTileEntityTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER, false);

    public static final BlockTypeTile<TileEntityPressurizedWaterReactorFuelAssembly> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          createInternal(MeknucReactorLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                () -> MeknucReactorTileEntityTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY);

    public static final BlockTypeTile<TileEntityPressurizedWaterReactorControlAssembly> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          createInternal(MeknucReactorLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                () -> MeknucReactorTileEntityTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType, boolean gui) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createPort(
          MeknucReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeStateReactorPortMode()});
        builder = (BlockTileBuilder) builder.externalMultiblock();
        return (BlockTypeTile<TILE>) builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          MeknucReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description))
              .internalMultiblock()
              .build();
    }

    private MeknucReactorBlockTypes() {
    }
}
