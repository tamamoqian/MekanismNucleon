package meknuc.turbine;

import java.util.function.Supplier;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCasing;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCondenser;
import meknuc.turbine.tile.TileEntityHighPressureTurbinePort;
import meknuc.turbine.tile.TileEntityHighPressureTurbineVent;

/**
 * 高压蒸汽涡轮的方块类型。
 * <p>
 * 外壳 / 端口（阀门）/ 排气口都是多方块外壳方块，分压层与冷凝器是内部方块；
 * GUI 直接挂载 Mekanism 工业涡轮的容器类型。
 */
public class MeknucTurbineBlockTypes {

    public static final BlockTypeTile<TileEntityHighPressureTurbineCasing> HIGH_PRESSURE_STEAM_TURBINE_CASING =
          createExternal(MeknucTurbineLang.HIGH_PRESSURE_STEAM_TURBINE_CASING,
                () -> MeknucTurbineTileEntityTypes.HIGH_PRESSURE_STEAM_TURBINE_CASING);

    public static final BlockTypeTile<TileEntityHighPressureTurbinePort> HIGH_PRESSURE_STEAM_TURBINE_PORT =
          createExternal(MeknucTurbineLang.HIGH_PRESSURE_STEAM_TURBINE_PORT,
                () -> MeknucTurbineTileEntityTypes.HIGH_PRESSURE_STEAM_TURBINE_PORT);

    public static final BlockTypeTile<TileEntityHighPressureTurbineVent> HIGH_PRESSURE_STEAM_TURBINE_VENT =
          createExternal(MeknucTurbineLang.HIGH_PRESSURE_STEAM_TURBINE_VENT,
                () -> MeknucTurbineTileEntityTypes.HIGH_PRESSURE_STEAM_TURBINE_VENT);

    public static final BlockTypeTile<TileEntityHighPressureTurbineCondenser> HIGH_PRESSURE_STEAM_CONDENSER =
          createInternal(MeknucTurbineLang.HIGH_PRESSURE_STEAM_CONDENSER,
                () -> MeknucTurbineTileEntityTypes.HIGH_PRESSURE_STEAM_CONDENSER);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(MeknucTurbineLang description,
          Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{new AttributeGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE,
              MeknucTurbineLang.HIGH_PRESSURE_TURBINE_GUI)});
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(MeknucTurbineLang description,
          Supplier<TileEntityTypeRegistryObject<TILE>> tileType) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
    }

    private MeknucTurbineBlockTypes() {
    }
}
