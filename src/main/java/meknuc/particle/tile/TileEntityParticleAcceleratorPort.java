package meknuc.particle.tile;

import java.util.List;
import mekanism.api.IConfigurable;
import mekanism.api.IContentsListener;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.blocks.MeknucParticleAcceleratorBlockTypes.Mode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 大型环形粒子加速器的端口方块实体。
 * <p>
 * 潜行右键循环切换端口模式：
 * <ul>
 *   <li>输入/输出粒子束：暴露多方块的粒子束化学品缓存；</li>
 *   <li>输入能量：只暴露能量缓存，且只能向里输入；</li>
 *   <li>输入/输出靶材：分别暴露靶材输入槽与输出槽。</li>
 * </ul>
 */
public class TileEntityParticleAcceleratorPort extends TileEntityParticleAcceleratorPart implements IConfigurable {

    private final List<BlockCapability<?, @Nullable Direction>> portCapabilities = List.of(
          Capabilities.ITEM.block(), Capabilities.CHEMICAL.block(), Capabilities.ENERGY.block());

    public TileEntityParticleAcceleratorPort(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public Mode getMode() {
        return getBlockState().getValue(meknuc.blocks.MeknucParticleAcceleratorBlockTypes.MODE);
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> getMultiblock().getInventorySlots(getMode());
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> java.util.Collections.emptyList();
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> getMultiblock().getChemicalTanks(getMode());
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return side -> getMultiblock().getEnergyContainers(getMode());
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        return type != ContainerType.HEAT && type != ContainerType.CHEMICAL && type != ContainerType.FLUID
              && type != ContainerType.ENERGY && super.persists(type);
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
                    invalidateCapabilitiesAll(portCapabilities);
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
