package meknuc.blocks;

import java.util.function.UnaryOperator;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * 大型环形粒子对撞机的控制器。
 * <p>
 * 放置时正面朝向玩家，整台机器的结构会向它的背后展开。
 * 创造模式下潜行右键可以直接铺出完整结构（测试用）。
 */
public class BlockParticleAcceleratorController extends BlockBasicMultiblock<TileEntityParticleAcceleratorController> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockParticleAcceleratorController(BlockTypeTile<TileEntityParticleAcceleratorController> type,
          UnaryOperator<BlockBehaviour.Properties> propertiesModifier) {
        super(type, propertiesModifier);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        return state.setValue(FACING, mirror.getRotation(facing).rotate(facing));
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (tryBuildTestStructure(level, pos, player)) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
          @NotNull Player player, @NotNull BlockHitResult hit) {
        if (tryBuildTestStructure(level, pos, player)) {
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    /**
     * 创造模式潜行右键：直接生成整台机器（并清掉包围盒内的其它方块）。
     */
    private static boolean tryBuildTestStructure(Level level, BlockPos pos, Player player) {
        if (!player.isCreative() || !player.isShiftKeyDown()) {
            return false;
        }
        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof TileEntityParticleAcceleratorController controller)) {
            return false;
        }
        if (!level.isClientSide) {
            controller.buildTestStructure(player);
        }
        return true;
    }
}
