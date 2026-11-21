package meknuc.wastestorage.tile;

import java.util.Collections;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.blocks.MeknucWasteStorageBlockTypes;
import meknuc.wastestorage.MeknucWasteStorageLang;
import meknuc.wastestorage.MeknucWasteStorageMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 耐辐射废料存储端口：按模式暴露多方块的物品槽位。
 * <ul>
 *     <li><b>输入废料</b>：只暴露废料空间，只能插入放射性物品，不能抽出；空间满了以后插入会被拒绝；</li>
 *     <li><b>输出衰变产物</b>：只暴露「衰变完成产物」空间（可抽出、自动推向相邻容器），
 *         另外废料空间里的燃料棒也能直接从输出端口抽出，但输出端口不接受任何插入。</li>
 * </ul>
 */
public class TileEntityWasteStoragePort extends TileEntityWasteStorageCasing {

    public TileEntityWasteStoragePort(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public MeknucWasteStorageBlockTypes.Mode getPortMode() {
        return getBlockState().getValue(MeknucWasteStorageBlockTypes.MODE);
    }

    /**
     * 手持放射性物品右键输入端口：直接塞进废料空间。
     */
    @Override
    public ItemInteractionResult onActivate(Player player, InteractionHand hand, ItemStack stack) {
        if (!isRemote() && !player.isShiftKeyDown() && getMultiblock().isFormed()
              && getPortMode() == MeknucWasteStorageBlockTypes.Mode.RADIOACTIVE_WASTE_INPUT
              && MeknucWasteStorageMultiblockData.isRadioactiveItem(stack)) {
            ItemStack remainder = getMultiblock().insertWaste(stack, Action.EXECUTE, AutomationType.MANUAL);
            player.setItemInHand(hand, remainder);
            if (!remainder.isEmpty()) {
                player.displayClientMessage(
                      MeknucWasteStorageLang.NO_ROOM.translateColored(EnumColor.RED), true);
            }
            return ItemInteractionResult.sidedSuccess(isRemote());
        }
        return super.onActivate(player, hand, stack);
    }

    /**
     * 配置工具潜行右键切换端口模式（输入废料 / 输出衰变产物）。
     */
    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            BlockState state = getBlockState();
            AttributeStateReactorPortMode<?> attribute = AttributeStateReactorPortMode.get(state);
            if (attribute != null) {
                BlockState next = attribute.nextMode(state);
                if (next != state) {
                    level.setBlockAndUpdate(worldPosition, next);
                    // 端口暴露的槽位视图随模式变化，必须让能力缓存失效
                    invalidateCapabilitiesFull();
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
        // 未成形时不对接管道，避免物品被塞进之后会被丢弃的默认数据里
        return side -> getMultiblock().isFormed() ? getMultiblock().getPortSlots(getPortMode())
              : Collections.emptyList();
    }
}
