package meknuc.blocks;

import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.blockentities.MeknucBlockEntityLogicAdapter;
import meknuc.blockentities.MeknucTileEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter extends MeknucBlockLogicAdapterBase {

    public static final AttributeStateLogicAdapterMode<MeknucLogicAdapterMode> MODE_ATTRIBUTE =
          new AttributeStateLogicAdapterMode<>(MODE, MeknucLogicAdapterMode.DISABLED);

    public static final BlockTypeTile<MeknucBlockEntityLogicAdapter> BLOCK_TYPE = createBlockType(
          () -> MeknucTileEntityTypes.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER, MeknucBlockReactorLang.HIGH_TEMPERATURE_GRAPHITE_GAS_COOLED_REACTOR_LOGIC_ADAPTER, MODE_ATTRIBUTE);

    public MeknucBlockHighTemperatureGraphiteGasCooledReactorLogicAdapter(BlockTypeTile<MeknucBlockEntityLogicAdapter> type, Properties properties) {
        super(type, properties);
    }
}
