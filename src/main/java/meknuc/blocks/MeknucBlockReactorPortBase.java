package meknuc.blocks;

import java.util.function.Supplier;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.blockentities.MeknucBlockEntityReactorPort;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class MeknucBlockReactorPortBase extends BlockTile<MeknucBlockEntityReactorPort, BlockTypeTile<MeknucBlockEntityReactorPort>> {

    protected MeknucBlockReactorPortBase(BlockTypeTile<MeknucBlockEntityReactorPort> type, Properties properties) {
        super(type, properties);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static BlockTypeTile<MeknucBlockEntityReactorPort> createBlockType(
          Supplier<TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort>> tileType, ILangEntry description,
          AttributeStateReactorPortMode<?> mode) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{mode});
        return (BlockTypeTile<MeknucBlockEntityReactorPort>) builder.build();
    }
}
