package meknuc.particle;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mekanism.common.MekanismLang;
import mekanism.common.lib.math.voxel.IShape;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.IMultiblock;
import mekanism.common.lib.multiblock.IStructureValidator;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.registries.GeneratorsBlocks;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

/**
 * 大型环形粒子对撞机的结构校验器。
 * <p>
 * 以控制器为锚点，按 {@link MeknucParticleAcceleratorPattern} 逐格比对方块：
 * <ul>
 *   <li>控制器正面 = 玩家放置时朝向的反方向，结构整体向控制器背后展开；</li>
 *   <li>控制器正面 3x3 范围内的防辐射外壳可以换成粒子加速器端口（其余位置不允许端口）；</li>
 *   <li>控制器后方第 3 格必须是粒子加速器靶件固定框架。</li>
 * </ul>
 */
public class MeknucParticleAcceleratorValidator implements IStructureValidator<MeknucParticleAcceleratorMultiblockData> {

    @Nullable
    private Level world;
    @Nullable
    private Structure structure;
    @Nullable
    private VoxelCuboid cuboid;
    @Nullable
    private TileEntityParticleAcceleratorController controller;

    /**
     * 由控制器朝向推导出的两根水平轴：{@code inward} 指向结构内部，{@code right} 对应图案的 +X。
     */
    private Direction inward = Direction.NORTH;
    private Direction right = Direction.EAST;

    @Override
    public void init(Level world, MultiblockManager<MeknucParticleAcceleratorMultiblockData> manager, Structure structure) {
        this.world = world;
        this.structure = structure;
        this.controller = null;
    }

    @Override
    public boolean precheck() {
        if (world == null || structure == null) {
            return false;
        }
        IMultiblock<?> master = structure.getController();
        if (!(master instanceof TileEntityParticleAcceleratorController tile)) {
            return false;
        }
        BlockState state = tile.getBlockState();
        if (!state.hasProperty(BlockParticleAcceleratorController.FACING)) {
            return false;
        }
        Direction front = state.getValue(BlockParticleAcceleratorController.FACING);
        inward = front.getOpposite();
        right = inward.getClockWise();
        controller = tile;
        cuboid = computeBounds(tile.getBlockPos());
        return quickCheck(tile.getBlockPos());
    }

    /**
     * 几个跨越整环的“特征方块”（都是钢质机壳），用来在真正逐格比对之前快速判断结构是否还差得远。
     * 建造过程中绝大多数判定会在这里就失败，避免每次都扫 3000 多个位置。
     */
    private static final int[][] SIGNATURE_CASINGS = {
          {20, 2, 0},
          {0, 2, 26},
          {46, 2, 20},
          {26, 5, 45}
    };

    private boolean quickCheck(BlockPos base) {
        for (int[] signature : SIGNATURE_CASINGS) {
            if (!checkSignature(base, signature[0], signature[1], signature[2], 'C')) {
                return false;
            }
        }
        return checkSignature(base, 23, 3, 43, 'T');
    }

    private boolean checkSignature(BlockPos base, int x, int y, int depth, char expected) {
        int dx = x - MeknucParticleAcceleratorPattern.CONTROLLER_X;
        int dy = y - MeknucParticleAcceleratorPattern.CONTROLLER_Y;
        int inwardDistance = MeknucParticleAcceleratorPattern.CONTROLLER_Z - depth;
        BlockPos pos = base.relative(right, dx).relative(Direction.UP, dy).relative(inward, inwardDistance);
        return WorldUtils.getBlockState(world, pos).map(state -> matches(expected, state)).orElse(false);
    }

    private VoxelCuboid computeBounds(BlockPos base) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int maxDx = MeknucParticleAcceleratorPattern.CONTROLLER_X;
        int maxDy = MeknucParticleAcceleratorPattern.CONTROLLER_Y;
        int maxDz = MeknucParticleAcceleratorPattern.CONTROLLER_Z;
        for (int dx : new int[]{-maxDx, maxDx}) {
            for (int dy : new int[]{-maxDy, maxDy}) {
                for (int depth : new int[]{0, maxDz}) {
                    BlockPos pos = base.relative(right, dx).relative(Direction.UP, dy).relative(inward, depth);
                    minX = Math.min(minX, pos.getX());
                    maxX = Math.max(maxX, pos.getX());
                    minY = Math.min(minY, pos.getY());
                    maxY = Math.max(maxY, pos.getY());
                    minZ = Math.min(minZ, pos.getZ());
                    maxZ = Math.max(maxZ, pos.getZ());
                }
            }
        }
        return new VoxelCuboid(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    @Override
    public FormationResult validate(FormationProtocol<MeknucParticleAcceleratorMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap) {
        if (world == null || controller == null) {
            return FormationResult.FAIL;
        }
        BlockPos base = controller.getBlockPos();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int maxX = MeknucParticleAcceleratorPattern.CONTROLLER_X;
        int maxY = MeknucParticleAcceleratorPattern.CONTROLLER_Y;
        for (int depth = 0; depth < MeknucParticleAcceleratorPattern.SIZE_Z; depth++) {
            String[] layer = MeknucParticleAcceleratorPattern.LAYERS[depth];
            int inwardDistance = MeknucParticleAcceleratorPattern.CONTROLLER_Z - depth;
            for (int y = 0; y < MeknucParticleAcceleratorPattern.SIZE_Y; y++) {
                String row = layer[y];
                int dy = y - maxY;
                for (int x = 0; x < MeknucParticleAcceleratorPattern.SIZE_X; x++) {
                    char expected = row.charAt(x);
                    if (expected == '.') {
                        continue;
                    }
                    int dx = x - maxX;
                    mutable.set(
                          base.getX() + right.getStepX() * dx + inward.getStepX() * inwardDistance,
                          base.getY() + dy,
                          base.getZ() + right.getStepZ() * dx + inward.getStepZ() * inwardDistance
                    );
                    Optional<BlockState> optionalState = WorldUtils.getBlockState(world, chunkMap, mutable);
                    if (optionalState.isEmpty()) {
                        return FormationResult.FAIL;
                    }
                    BlockState state = optionalState.get();
                    if (!matches(expected, state)) {
                        return FormationResult.fail(MekanismLang.MULTIBLOCK_INVALID_INNER, mutable, state);
                    }
                    ctx.locations.add(mutable.immutable());
                }
            }
        }
        // 控制器本身也要算进结构位置
        ctx.locations.add(base);
        return FormationResult.SUCCESS;
    }

    /**
     * 比对图案字符与实际方块。
     */
    private boolean matches(char expected, BlockState state) {
        Block block = state.getBlock();
        return switch (expected) {
            case 'A' -> isCasing(block);
            case 'C' -> block == MekanismBlocks.STEEL_CASING.value();
            case 'D' -> block == GeneratorsBlocks.REACTOR_GLASS.value();
            case 'K' -> block == MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATION_COIL.value();
            case 'T' -> block == MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME.value();
            case 'P' -> isCasing(block) || block == MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_PORT.value();
            default -> false;
        };
    }

    private static boolean isCasing(Block block) {
        return block == MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING.value();
    }

    @Override
    public FormationResult postcheck(MeknucParticleAcceleratorMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        // 登记几个“特征方块”，数据类每 200 tick 只抽查这些位置。
        // 结构正常时不会触发 recheckStructure，因此不会重置多方块数据或关闭 GUI。
        if (controller != null) {
            List<BlockPos> positions = new ArrayList<>();
            List<Block> blocks = new ArrayList<>();
            for (int[] signature : SIGNATURE_CASINGS) {
                positions.add(signaturePos(signature[0], signature[1], signature[2]));
                blocks.add(MekanismBlocks.STEEL_CASING.value());
            }
            positions.add(signaturePos(MeknucParticleAcceleratorPattern.CONTROLLER_X,
                  MeknucParticleAcceleratorPattern.CONTROLLER_Y,
                  MeknucParticleAcceleratorPattern.CONTROLLER_Z - MeknucParticleAcceleratorPattern.TARGET_DEPTH));
            blocks.add(MeknucParticleAcceleratorBlocks.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME.value());
            structure.setMonitoredBlocks(positions, blocks);
        }
        return FormationResult.SUCCESS;
    }

    private BlockPos signaturePos(int x, int y, int depth) {
        BlockPos base = controller.getBlockPos();
        int dx = x - MeknucParticleAcceleratorPattern.CONTROLLER_X;
        int dy = y - MeknucParticleAcceleratorPattern.CONTROLLER_Y;
        int inwardDistance = MeknucParticleAcceleratorPattern.CONTROLLER_Z - depth;
        return base.relative(right, dx).relative(Direction.UP, dy).relative(inward, inwardDistance);
    }

    @Override
    public IShape getShape() {
        return cuboid;
    }
}