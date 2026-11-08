package meknuc.reactor.tile;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import meknuc.items.MeknucItemBase;
import meknuc.reactor.AttributeStateBwrLogicAdapterMode;
import meknuc.reactor.BwrLogicAdapterMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityBwrLogicAdapter extends TileEntityBwrPart {

    public BwrLogicAdapterMode logicType = BwrLogicAdapterMode.DISABLED;

    public TileEntityBwrLogicAdapter(BlockPos pos, BlockState state) {
        super(pos, state);
        AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
        if (attribute != null) {
            logicType = state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty);
        }
    }

    @Override
    public boolean canBeMaster() {
        return true;
    }

    public BwrLogicAdapterMode getMode() {
        return logicType;
    }

    public BwrLogicAdapterMode[] getModes() {
        return BwrLogicAdapterMode.MODES;
    }

    public void setLogicTypeFromPacket(BwrLogicAdapterMode logicType) {
        if (this.logicType != logicType) {
            this.logicType = logicType;
            markForSave();
        }
        BlockState state = getBlockState();
        AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
        if (attribute != null && state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty) != logicType) {
            level.setBlockAndUpdate(worldPosition,
                  state.setValue(AttributeStateBwrLogicAdapterMode.modeProperty, logicType));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        BlockState state = getBlockState();
        AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
        if (attribute != null) {
            builder.set(MeknucItemBase.BWR_LOGIC_TYPE, state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty));
        } else {
            builder.set(MeknucItemBase.BWR_LOGIC_TYPE, logicType);
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.logicType = input.getOrDefault(MeknucItemBase.BWR_LOGIC_TYPE, this.logicType);
        if (level != null) {
            BlockState state = getBlockState();
            AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
            if (attribute != null && state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty) != this.logicType) {
                level.setBlockAndUpdate(worldPosition,
                      state.setValue(AttributeStateBwrLogicAdapterMode.modeProperty, this.logicType));
            }
        }
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            BlockState state = getBlockState();
            AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
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
}
