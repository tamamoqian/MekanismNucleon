package meknuc.reactor.tile;

import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.blocks.MeknucFbrBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 快中子增殖堆端口：按模式向外暴露不同的能力。
 *
 * <ul>
 *   <li>输入冷却剂：只接受钠</li>
 *   <li>输出热冷却剂：只输出过热钠</li>
 *   <li>输入燃料：只接受快堆燃料棒</li>
 *   <li>废料输出口：只输出耗尽的热燃料棒</li>
 *   <li>增殖材料输入：只接受铀-238粉</li>
 *   <li>增殖材料输出：只输出钚-239粉</li>
 * </ul>
 */
public class TileEntityFbrPort extends TileEntityFbrPart {

    private final List<BlockCapability<?, @Nullable Direction>> portCapabilities = List.of(
          Capabilities.ITEM.block(), Capabilities.CHEMICAL.block());

    public TileEntityFbrPort(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public MeknucFbrBlockTypes.Mode getMode() {
        return getBlockState().getValue(MeknucFbrBlockTypes.MODE);
    }

    public void setMode(MeknucFbrBlockTypes.Mode mode) {
        if (mode != getMode()) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MeknucFbrBlockTypes.MODE, mode));
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
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> getMultiblock().getChemicalTanks(getMode());
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        return type != ContainerType.HEAT && type != ContainerType.CHEMICAL && type != ContainerType.ITEM && super.persists(type);
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            MeknucFbrBlockTypes.Mode mode = getMode();
            MeknucFbrBlockTypes.Mode[] values = MeknucFbrBlockTypes.Mode.values();
            MeknucFbrBlockTypes.Mode next = values[(mode.ordinal() + 1) % values.length];
            setMode(next);
            player.displayClientMessage(MekanismLang.BOILER_VALVE_MODE_CHANGE.translateColored(
                  EnumColor.GRAY, next.getTextComponent()), true);
        }
        return InteractionResult.SUCCESS;
    }
}
