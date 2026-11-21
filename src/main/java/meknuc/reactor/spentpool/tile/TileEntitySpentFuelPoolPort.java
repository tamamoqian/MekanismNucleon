package meknuc.reactor.spentpool.tile;

import java.util.Collections;
import java.util.List;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolBlockTypes;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 乏燃料池端口：按模式暴露多方块的水/蒸汽储罐或燃料棒槽位。
 */
public class TileEntitySpentFuelPoolPort extends TileEntitySpentFuelPoolCasing {

    public TileEntitySpentFuelPoolPort(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public MeknucSpentFuelPoolBlockTypes.Mode getPortMode() {
        return getBlockState().getValue(MeknucSpentFuelPoolBlockTypes.MODE);
    }

    @Override
    public ItemInteractionResult onActivate(Player player, InteractionHand hand, ItemStack stack) {
        if (MeknucSpentFuelPoolMultiblockData.isAcceptedFuel(stack)) {
            if (!isRemote()) {
                if (!getMultiblock().hasFreeThread()) {
                    player.displayClientMessage(
                          Component.translatable("gui.meknuc.spent_fuel_pool.no_free_threads"), true);
                } else {
                    ItemStack remainder = getMultiblock().insertFuel(stack, Action.EXECUTE);
                    player.setItemInHand(hand, remainder);
                }
            }
            return ItemInteractionResult.sidedSuccess(isRemote());
        }
        return super.onActivate(player, hand, stack);
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
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        // 物品能力与模式无关：输入槽只接受热乏燃料，输出槽只可抽取，
        // 这样无论端口处于哪种模式都能接入漏斗/管道。
        return side -> getMultiblock().getInventorySlots(null);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> switch (getPortMode()) {
            case COOLANT_INPUT -> List.of(getMultiblock().getWaterTank());
            default -> Collections.emptyList();
        };
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> switch (getPortMode()) {
            case COOLANT_OUTPUT -> List.of(getMultiblock().getSteamTank());
            default -> Collections.emptyList();
        };
    }
}
