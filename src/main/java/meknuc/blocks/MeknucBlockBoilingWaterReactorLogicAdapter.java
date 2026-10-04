package meknuc.blocks;

import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucTileEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MeknucBlockBoilingWaterReactorLogicAdapter extends MeknucBlockLogicAdapterBase {

    public static final AttributeStateLogicAdapterMode<MeknucLogicAdapterMode> MODE_ATTRIBUTE =
          new AttributeStateLogicAdapterMode<>(MODE, MeknucLogicAdapterMode.DISABLED);

    public static final BlockTypeTile<MeknucBlockEntityLogicAdapter> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER, MeknucBlockReactorLang.BOILING_WATER_REACTOR_LOGIC_ADAPTER, MODE_ATTRIBUTE);

    public MeknucBlockBoilingWaterReactorLogicAdapter(BlockTypeTile<MeknucBlockEntityLogicAdapter> type, Properties properties) {
        super(type, properties);
    }
}
