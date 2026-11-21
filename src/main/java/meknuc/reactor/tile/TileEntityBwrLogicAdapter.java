package meknuc.reactor.tile;

import java.util.EnumSet;
import java.util.function.IntFunction;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTranslationKey.IHasEnumNameTranslationKey;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import meknuc.reactor.AttributeStateBwrLogicAdapterMode;
import meknuc.reactor.BwrLogicAdapterMode;
import meknuc.reactor.MeknucBwrConstants;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrMultiblockData;
import meknuc.reactor.MeknucReactorComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public class TileEntityBwrLogicAdapter extends TileEntityBwrPart {

    private static final int REDSTONE_MAX = 15;

    public BwrLogicAdapterMode logicType = BwrLogicAdapterMode.DISABLED;
    private RedstoneStatus prevStatus = RedstoneStatus.IDLE;

    public TileEntityBwrLogicAdapter(BlockPos pos, BlockState state) {
        super(pos, state);
        setControlType(RedstoneControl.HIGH);
        AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
        if (attribute != null) {
            logicType = state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty);
        }
    }

    @Override
    protected boolean onUpdateServer(MeknucBwrMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (logicType == BwrLogicAdapterMode.CONTROL_ROD && multiblock.isFormed()) {
            Level world = getLevel();
            if (world != null) {
                int signal = world.getBestNeighborSignal(worldPosition);
                multiblock.setControlRodInsertion(Math.round(signal * 100.0F / REDSTONE_MAX));
            }
        }
        RedstoneStatus status = getStatus();
        if (status != prevStatus) {
            Level world = getLevel();
            if (world != null) {
                Direction side = multiblock.getOutsideSide(worldPosition);
                BlockState state = getBlockState();
                if (side == null) {
                    world.updateNeighborsAt(getBlockPos(), state.getBlock());
                } else if (!EventHooks.onNeighborNotify(world, worldPosition, state, EnumSet.of(side), false)
                      .isCanceled()) {
                    world.neighborChanged(worldPosition.relative(side), state.getBlock(), worldPosition);
                }
            }
            prevStatus = status;
        }
        return needsPacket;
    }

    public int getRedstoneLevel(Direction side) {
        return !isRemote()
              && getMultiblock().isPositionOutsideBounds(worldPosition.relative(side))
              && getStatus() == RedstoneStatus.OUTPUTTING ? 15 : 0;
    }

    public RedstoneStatus getStatus() {
        if (isRemote()) {
            return prevStatus;
        }
        MeknucBwrMultiblockData multiblock = getMultiblock();
        if (multiblock.isFormed()) {
            switch (logicType) {
                case ACTIVATION:
                    if (canFunction()) {
                        return RedstoneStatus.POWERED;
                    }
                    break;
                case CONTROL_ROD:
                    if (canFunction()) {
                        return RedstoneStatus.POWERED;
                    }
                    break;
                case HIGH_TEMPERATURE:
                    if (multiblock.getTemperature() >= MeknucBwrConstants.HIGH_TEMPERATURE_THRESHOLD) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case CRITICAL_WASTE_LEVEL:
                    int critical = (int) Math.ceil(multiblock.getCacheCapacity() * 0.9);
                    if (critical > 0 && multiblock.getWasteCount() >= critical) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case DAMAGED:
                    if (multiblock.isMeltedDown() || multiblock.getMeltdownTimer() > 0) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case DISABLED:
                    break;
            }
        }
        return RedstoneStatus.IDLE;
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
    public boolean supportsMode(RedstoneControl mode) {
        return super.supportsMode(mode) && mode != RedstoneControl.DISABLED;
    }

    @Override
    public void onPowerChange() {
        super.onPowerChange();
        if (!isRemote()) {
            MeknucBwrMultiblockData multiblock = getMultiblock();
            if (multiblock.isFormed() && logicType == BwrLogicAdapterMode.ACTIVATION) {
                multiblock.setActive(canFunction());
            }
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(BwrLogicAdapterMode.BY_ID, BwrLogicAdapterMode.DISABLED,
              this::getMode, value -> logicType = value));
        container.track(SyncableEnum.create(RedstoneStatus.BY_ID, RedstoneStatus.IDLE,
              () -> prevStatus, value -> prevStatus = value));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        BlockState state = getBlockState();
        AttributeStateBwrLogicAdapterMode<?> attribute = AttributeStateBwrLogicAdapterMode.get(state);
        if (attribute != null) {
            builder.set(MeknucReactorComponents.BWR_LOGIC_TYPE, state.getValue(AttributeStateBwrLogicAdapterMode.modeProperty));
        } else {
            builder.set(MeknucReactorComponents.BWR_LOGIC_TYPE, logicType);
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.logicType = input.getOrDefault(MeknucReactorComponents.BWR_LOGIC_TYPE, this.logicType);
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
                    logicType = next.getValue(AttributeStateBwrLogicAdapterMode.modeProperty);
                    markForSave();
                    level.setBlockAndUpdate(worldPosition, next);
                    player.displayClientMessage(MeknucBwrLang.LOGIC_MODE_CHANGE.translateColored(EnumColor.GRAY,
                          attribute.getModeName(next)), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public enum RedstoneStatus implements IHasEnumNameTranslationKey {
        IDLE(MekanismLang.IDLE),
        OUTPUTTING(MeknucBwrLang.LOGIC_OUTPUTTING),
        POWERED(MeknucBwrLang.LOGIC_POWERED);

        public static final IntFunction<RedstoneStatus> BY_ID =
              ByIdMap.continuous(Enum::ordinal, values(), OutOfBoundsStrategy.WRAP);

        private final ILangEntry name;

        RedstoneStatus(ILangEntry name) {
            this.name = name;
        }

        @Override
        public String getTranslationKey() {
            return name.getTranslationKey();
        }
    }
}
