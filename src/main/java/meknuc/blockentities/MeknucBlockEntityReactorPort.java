package meknuc.blockentities;

import mekanism.api.IConfigurable;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.AttributeStateReactorPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MeknucBlockEntityReactorPort extends TileEntityMekanism implements IConfigurable {

    public MeknucBlockEntityReactorPort(BlockPos pos, BlockState state) {
        super(state.getBlockHolder(), pos, state);
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            BlockState state = getBlockState();
            AttributeStateReactorPortMode<?> attribute = AttributeStateReactorPortMode.get(state);
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
