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
import mekanism.common.util.NBTUtils;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucPwrMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class TileEntityPwrLogicAdapter extends TileEntityPwrPart {

    private static final int REDSTONE_MAX = 15;
    private static final int CRITICAL_WASTE_LEVEL =
          (int) Math.ceil(MeknucPwrMultiblockData.CACHE_CAPACITY * 0.9);

    public PressurizedWaterReactorLogic logicType = PressurizedWaterReactorLogic.DISABLED;
    private RedstoneStatus prevStatus = RedstoneStatus.IDLE;

    public TileEntityPwrLogicAdapter(BlockPos pos, BlockState state) {
        super(pos, state);
        setControlType(RedstoneControl.HIGH);
    }

    @Override
    protected boolean onUpdateServer(MeknucPwrMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (logicType == PressurizedWaterReactorLogic.CONTROL_ROD && multiblock.isFormed()) {
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

    public PressurizedWaterReactorLogic getMode() {
        return logicType;
    }

    public PressurizedWaterReactorLogic[] getModes() {
        return PressurizedWaterReactorLogic.values();
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
        MeknucPwrMultiblockData multiblock = getMultiblock();
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
                    if (multiblock.getTemperature() >= MeknucPwrMultiblockData.DAMAGE_TEMPERATURE) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case CRITICAL_WASTE_LEVEL:
                    if (multiblock.getWasteCount() >= CRITICAL_WASTE_LEVEL) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case DAMAGED:
                    if (multiblock.getDamage() > 0.0) {
                        return RedstoneStatus.OUTPUTTING;
                    }
                    break;
                case DISABLED:
                    break;
            }
        }
        return RedstoneStatus.IDLE;
    }

    public void setLogicTypeFromPacket(PressurizedWaterReactorLogic logicType) {
        if (this.logicType != logicType) {
            this.logicType = logicType;
            markForSave();
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
            MeknucPwrMultiblockData multiblock = getMultiblock();
            if (multiblock.isFormed() && logicType == PressurizedWaterReactorLogic.ACTIVATION) {
                multiblock.setActive(canFunction());
            }
        }
    }

    @Override
    public void readSustainedData(Provider provider, @NotNull CompoundTag nbt) {
        super.readSustainedData(provider, nbt);
        NBTUtils.setEnumIfPresent(nbt, "logic_type", PressurizedWaterReactorLogic.BY_ID,
              logicType -> this.logicType = logicType);
    }

    @Override
    public void writeSustainedData(Provider provider, @NotNull CompoundTag nbtTags) {
        super.writeSustainedData(provider, nbtTags);
        NBTUtils.writeEnum(nbtTags, "logic_type", logicType);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(PressurizedWaterReactorLogic.BY_ID, PressurizedWaterReactorLogic.DISABLED,
              this::getMode, value -> logicType = value));
        container.track(SyncableEnum.create(RedstoneStatus.BY_ID, RedstoneStatus.IDLE,
              () -> prevStatus, value -> prevStatus = value));
    }

    @Override
    public boolean canBeMaster() {
        return false;
    }

    public enum PressurizedWaterReactorLogic implements IHasEnumNameTranslationKey {
        DISABLED(MeknucPwrLang.LOGIC_DISABLED, MeknucPwrLang.DESCRIPTION_LOGIC_DISABLED,
              new ItemStack(Items.GUNPOWDER), EnumColor.DARK_GRAY),
        ACTIVATION(MeknucPwrLang.LOGIC_ACTIVATION, MeknucPwrLang.DESCRIPTION_LOGIC_ACTIVATION,
              new ItemStack(Items.FLINT_AND_STEEL), EnumColor.AQUA),
        CONTROL_ROD(MeknucPwrLang.LOGIC_CONTROL_ROD, MeknucPwrLang.DESCRIPTION_LOGIC_CONTROL_ROD,
              new ItemStack(Items.LEVER), EnumColor.BRIGHT_GREEN),
        HIGH_TEMPERATURE(MeknucPwrLang.LOGIC_HIGH_TEMPERATURE, MeknucPwrLang.DESCRIPTION_LOGIC_HIGH_TEMPERATURE,
              new ItemStack(Items.REDSTONE), EnumColor.RED),
        CRITICAL_WASTE_LEVEL(MeknucPwrLang.LOGIC_CRITICAL_WASTE_LEVEL,
              MeknucPwrLang.DESCRIPTION_LOGIC_CRITICAL_WASTE_LEVEL, new ItemStack(Items.REDSTONE), EnumColor.RED),
        DAMAGED(MeknucPwrLang.LOGIC_DAMAGED, MeknucPwrLang.DESCRIPTION_LOGIC_DAMAGED,
              new ItemStack(Items.REDSTONE), EnumColor.RED);

        public static final IntFunction<PressurizedWaterReactorLogic> BY_ID =
              ByIdMap.continuous(Enum::ordinal, values(), OutOfBoundsStrategy.WRAP);

        private final ILangEntry name;
        private final ILangEntry description;
        private final ItemStack renderStack;
        private final EnumColor color;

        PressurizedWaterReactorLogic(ILangEntry name, ILangEntry description, ItemStack renderStack, EnumColor color) {
            this.name = name;
            this.description = description;
            this.renderStack = renderStack;
            this.color = color;
        }

        public ItemStack getRenderStack() {
            return renderStack;
        }

        @Override
        public String getTranslationKey() {
            return name.getTranslationKey();
        }

        public Component getDescription() {
            return description.translate();
        }

        public EnumColor getColor() {
            return color;
        }
    }

    public enum RedstoneStatus implements IHasEnumNameTranslationKey {
        IDLE(MekanismLang.IDLE),
        OUTPUTTING(MeknucPwrLang.LOGIC_OUTPUTTING),
        POWERED(MeknucPwrLang.LOGIC_POWERED);

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
