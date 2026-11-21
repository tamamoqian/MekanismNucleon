package meknuc.particle.tile;

import java.util.Collections;
import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import meknuc.blocks.BlockParticleAcceleratorController;
import meknuc.blocks.MeknucParticleAcceleratorBlocks;
import meknuc.particle.MeknucParticleAcceleratorPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 大型环形粒子加速机的控制器方块实体。
 * <p>
 * 结构以这个方块为锚点展开：它的正面朝向玩家，结构向它背后延伸。
 * 成形后右键打开粒子加速器 GUI（选择配方、查看能量 / 进度 / 粒子束缓存）。
 */
public class TileEntityParticleAcceleratorController extends TileEntityParticleAcceleratorPart {

    public TileEntityParticleAcceleratorController(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean canBeMaster() {
        return true;
    }

    /**
     * 控制器本身也暴露靶材输入 / 输出槽，物品管道可以直接接在控制器上把靶材送进机器
     * （不强制非要在 3×3 端口区里放「输入靶材」端口）。
     */
    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> getMultiblock().getTargetSlots();
    }

    /**
     * 控制器正面的朝向（结构向它的反方向展开）。
     */
    public Direction getFacing() {
        BlockState state = getBlockState();
        return state.hasProperty(BlockParticleAcceleratorController.FACING)
              ? state.getValue(BlockParticleAcceleratorController.FACING)
              : Direction.SOUTH;
    }

    /**
     * 未成形时右键触发一次结构检测；成形后右键打开 GUI。
     */
    @Override
    public ItemInteractionResult onActivate(Player player, InteractionHand hand, ItemStack stack) {
        if (!isRemote()) {
            if (!getMultiblock().isFormed()) {
                FormationResult result = getStructure().runUpdate(this);
                if (!result.isFormed() && result.getResultText() != null) {
                    player.sendSystemMessage(result.getResultText());
                }
                if (result.isFormed()) {
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (!player.isShiftKeyDown()) {
                openGui(player);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.onActivate(player, hand, stack);
    }

    /**
     * 空手右键：未成形时尝试检测结构，成形后打开 GUI。
     */
    @Override
    public InteractionResult onRightClick(Player player) {
        if (!isRemote()) {
            if (!getMultiblock().isFormed()) {
                FormationResult result = getStructure().runUpdate(this);
                if (!result.isFormed() && result.getResultText() != null) {
                    player.sendSystemMessage(result.getResultText());
                }
                return InteractionResult.SUCCESS;
            }
            if (!player.isShiftKeyDown()) {
                openGui(player);
                return InteractionResult.SUCCESS;
            }
        }
        return super.onRightClick(player);
    }

    /**
     * 测试用：按图案在控制器周围直接铺出完整结构。
     * <p>
     * 会先清空整个 47 x 7 x 47 包围盒（控制器自身保留），再按图案放置方块。
     * 只应在创造模式下由 {@link BlockParticleAcceleratorController} 调用。
     */
    public void buildTestStructure(@Nullable Player player) {
        if (level == null || level.isClientSide) {
            return;
        }
        Direction inward = getFacing().getOpposite();
        Direction right = inward.getClockWise();
        BlockPos base = worldPosition;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int depth = 0; depth < MeknucParticleAcceleratorPattern.SIZE_Z; depth++) {
            int inwardDistance = MeknucParticleAcceleratorPattern.CONTROLLER_Z - depth;
            for (int y = 0; y < MeknucParticleAcceleratorPattern.SIZE_Y; y++) {
                int dy = y - MeknucParticleAcceleratorPattern.CONTROLLER_Y;
                String row = MeknucParticleAcceleratorPattern.LAYERS[depth][y];
                for (int x = 0; x < MeknucParticleAcceleratorPattern.SIZE_X; x++) {
                    int dx = x - MeknucParticleAcceleratorPattern.CONTROLLER_X;
                    cursor.set(
                          base.getX() + right.getStepX() * dx + inward.getStepX() * inwardDistance,
                          base.getY() + dy,
                          base.getZ() + right.getStepZ() * dx + inward.getStepZ() * inwardDistance
                    );
                    if (cursor.equals(base)) {
                        // 控制器所在的位置保持不动
                        continue;
                    }
                    BlockState target = stateFor(row.charAt(x), x, y, depth);
                    if (target == null) {
                        if (!level.isEmptyBlock(cursor)) {
                            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    } else {
                        level.setBlock(cursor, target, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
        // 结构图可能还是刚放下时的单方块状态，先重建再让它在下一 tick 成形
        Structure structure = getStructure();
        if (!structure.isValid()) {
            structure.tick(this, true);
            structure = getStructure();
        }
        structure.markForUpdate(level, false);
        if (player != null) {
            player.displayClientMessage(Component.translatable("message.meknuc.particle_accelerator.structure_built"), true);
        }
    }

    /**
     * 图案字符对应的方块状态；{@code null} 表示这里要清空。
     */
    @Nullable
    private static BlockState stateFor(char pattern, int x, int y, int depth) {
        return switch (pattern) {
            case 'A' -> MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING.defaultState();
            case 'C' -> MekanismBlocks.STEEL_CASING.defaultState();
            case 'D' -> GeneratorsBlocks.REACTOR_GLASS.defaultState();
            case 'K' -> MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATION_COIL.defaultState();
            case 'T' -> MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME.defaultState();
            // 控制器右手边那一格放一个端口作为示例，其余可替换位置仍用外壳
            case 'P' -> x == MeknucParticleAcceleratorPattern.CONTROLLER_X + 1
                  && y == MeknucParticleAcceleratorPattern.CONTROLLER_Y
                  && depth == MeknucParticleAcceleratorPattern.CONTROLLER_Z
                  ? MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT.defaultState()
                  : MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING.defaultState();
            default -> null;
        };
    }
}
