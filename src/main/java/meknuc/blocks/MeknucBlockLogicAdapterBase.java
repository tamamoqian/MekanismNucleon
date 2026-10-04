package meknuc.blocks;

import java.util.function.Supplier;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class MeknucBlockLogicAdapterBase extends BlockTile<MeknucBlockEntityLogicAdapter, BlockTypeTile<MeknucBlockEntityLogicAdapter>> {

    public static final EnumProperty<MeknucLogicAdapterMode> MODE = EnumProperty.create("mode", MeknucLogicAdapterMode.class);

    protected MeknucBlockLogicAdapterBase(BlockTypeTile<MeknucBlockEntityLogicAdapter> type, Properties properties) {
        super(type, properties);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static BlockTypeTile<MeknucBlockEntityLogicAdapter> createBlockType(
          Supplier<TileEntityTypeRegistryObject<MeknucBlockEntityLogicAdapter>> tileType, ILangEntry description,
          AttributeStateLogicAdapterMode<?> mode) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{mode});
        return (BlockTypeTile<MeknucBlockEntityLogicAdapter>) builder.build();
    }
}
