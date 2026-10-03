package meknuc.reactor.tile;

import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import meknuc.reactor.AttributeStateReactorPortMode;
import meknuc.reactor.AttributeStateReactorPortMode.ReactorPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityPressurizedWaterReactorPort extends TileEntityPressurizedWaterReactorPart {

    private final List<BlockCapability<?, @Nullable Direction>> portCapabilities = List.of(Capabilities.ITEM.block(),
          Capabilities.FLUID.block(), Capabilities.CHEMICAL.block());

    public TileEntityPressurizedWaterReactorPort(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public ReactorPortMode getMode() {
        return getBlockState().getValue(AttributeStateReactorPortMode.modeProperty);
    }

    public void setMode(ReactorPortMode mode) {
        if (mode != getMode()) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AttributeStateReactorPortMode.modeProperty, mode));
            invalidateCapabilitiesAll(portCapabilities);
        }
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> getMultiblock().getInventorySlots(getMode());
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> getMultiblock().getFluidTanks(getMode());
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> getMultiblock().getChemicalTanks(getMode());
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            ReactorPortMode mode = getMode().getNext();
            setMode(mode);
            player.displayClientMessage(MekanismLang.BOILER_VALVE_MODE_CHANGE.translateColored(EnumColor.GRAY, mode), true);
        }
        return InteractionResult.SUCCESS;
    }
}
