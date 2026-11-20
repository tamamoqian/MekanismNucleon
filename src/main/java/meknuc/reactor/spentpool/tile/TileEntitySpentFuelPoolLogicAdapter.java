package meknuc.reactor.spentpool.tile;

import mekanism.api.IConfigurable;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import meknuc.blocks.AttributeStateLogicAdapterMode;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 乏燃料水池逻辑适配器：只作为结构外壳的一部分，潜行右键可切换模式。
 */
public class TileEntitySpentFuelPoolLogicAdapter extends TileEntityStructuralMultiblock implements IConfigurable {

    public TileEntitySpentFuelPoolLogicAdapter(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public boolean canInterface(MultiblockManager<?> manager) {
        return manager == MeknucSpentFuelPoolMultiblock.MANAGER;
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            BlockState state = getBlockState();
            AttributeStateLogicAdapterMode<?> attribute = AttributeStateLogicAdapterMode.get(state);
            if (attribute != null) {
                BlockState next = attribute.nextMode(state);
                if (next != state) {
                    level.setBlockAndUpdate(worldPosition, next);
                    player.displayClientMessage(MekanismLang.BOILER_VALVE_MODE_CHANGE.translateColored(EnumColor.GRAY,
                          attribute.getModeName(next)), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @NotNull
    @Override
    public InteractionResult onRightClick(Player player) {
        return InteractionResult.PASS;
    }
}
