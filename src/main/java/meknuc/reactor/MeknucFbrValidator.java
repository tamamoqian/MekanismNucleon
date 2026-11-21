package meknuc.reactor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.FormationProtocol.CasingType;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.FormationProtocol.StructureRequirement;
import mekanism.common.util.EnumUtils;
import meknuc.blocks.MeknucFbrBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * 快中子增殖堆结构校验器。
 *
 * <p>形状规则（定稿）：</p>
 * <ul>
 *   <li>底面与顶层都是 N×N 全外壳（N 取 9~13）</li>
 *   <li>中间层是 (N-2)×(N-2) 的正方形环，并且四个角各向外伸出 2 格棱边
 *       （沿 X 一格、沿 Z 一格，斜角那格不加），整层外接框仍是 N×N</li>
 *   <li>中间层内部最外一圈必须是中子反射层包壳，包壳圈里按燃料晶格摆放</li>
 *   <li>燃料组件只允许斜角相邻，正交方向上的格子必须留空</li>
 *   <li>顶部下方那一层把燃料组件原位置换成控制组件</li>
 *   <li>总高度 5~18 层</li>
 * </ul>
 */
public class MeknucFbrValidator extends CuboidStructureValidator<MeknucFbrMultiblockData> {

    private static Block reactorGlass;

    private VoxelCuboid bounds;
    private FormationResult shapeFailure;
    private int baseY;
    private int topY;
    private int minX;
    private int minZ;
    private int size;

    @Override
    public boolean precheck() {
        bounds = null;
        shapeFailure = null;
        if (structure.getController() == null) {
            return false;
        }
        BlockPos start = structure.getController().getBlockPos();
        Set<BlockPos> shell = new HashSet<>();
        List<BlockPos> open = new ArrayList<>();
        shell.add(start);
        open.add(start);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        while (!open.isEmpty()) {
            BlockPos ptr = open.remove(open.size() - 1);
            for (Direction side : EnumUtils.DIRECTIONS) {
                mutable.setWithOffset(ptr, side);
                BlockPos next = mutable.immutable();
                if (!shell.contains(next) && structure.contains(next)) {
                    shell.add(next);
                    open.add(next);
                }
            }
        }
        if (shell.isEmpty()) {
            return false;
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : shell) {
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        int height = maxY - minY + 1;
        if (height < MeknucFbrMultiblockData.MIN_HEIGHT || height > MeknucFbrMultiblockData.MAX_HEIGHT) {
            shapeFailure = FormationResult.fail(MeknucFbrLang.INVALID_HEIGHT);
            return false;
        }
        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;
        if (width != depth) {
            shapeFailure = FormationResult.fail(MeknucFbrLang.INVALID_SHAPE);
            return false;
        }
        if (width < MeknucFbrMultiblockData.MIN_SIZE || width > MeknucFbrMultiblockData.MAX_SIZE) {
            shapeFailure = FormationResult.fail(MeknucFbrLang.INVALID_SIZE);
            return false;
        }
        int[][] layers = new int[height][];
        for (BlockPos pos : shell) {
            int idx = pos.getY() - minY;
            int[] layer = layers[idx];
            if (layer == null) {
                layers[idx] = new int[]{pos.getX(), pos.getX(), pos.getZ(), pos.getZ()};
            } else {
                layer[0] = Math.min(layer[0], pos.getX());
                layer[1] = Math.max(layer[1], pos.getX());
                layer[2] = Math.min(layer[2], pos.getZ());
                layer[3] = Math.max(layer[3], pos.getZ());
            }
        }
        for (int i = 0; i < height; i++) {
            int[] layer = layers[i];
            if (layer == null
                  || layer[0] != minX || layer[1] != maxX
                  || layer[2] != minZ || layer[3] != maxZ) {
                shapeFailure = FormationResult.fail(MeknucFbrLang.INVALID_SHAPE);
                return false;
            }
        }
        this.baseY = minY;
        this.topY = maxY;
        this.minX = minX;
        this.minZ = minZ;
        this.size = width;
        this.bounds = new VoxelCuboid(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
        cuboid = bounds;
        return true;
    }

    @Override
    public FormationResult validate(FormationProtocol<MeknucFbrMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap) {
        if (!precheck()) {
            return shapeFailure == null ? FormationResult.FAIL : shapeFailure;
        }
        return super.validate(ctx, chunkMap);
    }

    @Override
    public void loadCuboid(VoxelCuboid cuboid) {
        super.loadCuboid(cuboid);
        bounds = cuboid;
        baseY = cuboid.getMinPos().getY();
        topY = cuboid.getMaxPos().getY();
        minX = cuboid.getMinPos().getX();
        minZ = cuboid.getMinPos().getZ();
        size = cuboid.getMaxPos().getX() - minX + 1;
    }

    private boolean isPlateLayer(int y) {
        return y == baseY || y == topY;
    }

    /** 中间层四个角各向外伸出的 2 格棱边（沿 X、沿 Z 各一格，斜角那格不加）。 */
    private boolean isEdgeStub(int x, int z) {
        int a = minX + 1;
        int b = minX + size - 2;
        int c = minZ + 1;
        int d = minZ + size - 2;
        if ((x == minX || x == minX + size - 1) && (z == c || z == d)) {
            return true;
        }
        return (z == minZ || z == minZ + size - 1) && (x == a || x == b);
    }

    /** 该坐标是否属于这座反应堆。 */
    private boolean exists(BlockPos pos) {
        int y = pos.getY();
        if (y < baseY || y > topY) {
            return false;
        }
        int x = pos.getX();
        int z = pos.getZ();
        if (isPlateLayer(y)) {
            return x >= minX && x <= minX + size - 1 && z >= minZ && z <= minZ + size - 1;
        }
        boolean innerSquare = x > minX && x < minX + size - 1 && z > minZ && z < minZ + size - 1;
        return innerSquare || isEdgeStub(x, z);
    }

    @Override
    protected FormationResult validateNode(FormationProtocol<MeknucFbrMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        if (!exists(pos)) {
            return FormationResult.SUCCESS;
        }
        return super.validateNode(ctx, chunkMap, pos);
    }

    @Override
    protected StructureRequirement getStructureRequirement(BlockPos pos) {
        if (bounds == null) {
            return super.getStructureRequirement(pos);
        }
        if (!exists(pos)) {
            return StructureRequirement.IGNORED;
        }
        int x = pos.getX();
        int z = pos.getZ();
        int y = pos.getY();
        if (isPlateLayer(y)) {
            boolean ring = x == minX || x == minX + size - 1 || z == minZ || z == minZ + size - 1;
            return ring ? StructureRequirement.FRAME : StructureRequirement.OTHER;
        }
        if (isEdgeStub(x, z)) {
            return StructureRequirement.FRAME;
        }
        boolean ring = x == minX + 1 || x == minX + size - 2 || z == minZ + 1 || z == minZ + size - 2;
        return ring ? StructureRequirement.FRAME : StructureRequirement.INNER;
    }

    @Override
    protected CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_CASING)) {
            return CasingType.FRAME;
        }
        if (BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_PORT)) {
            return CasingType.VALVE;
        }
        if (BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_LOGIC_ADAPTER)) {
            return CasingType.OTHER;
        }
        if (isReactorGlass(block)) {
            return CasingType.OTHER;
        }
        return CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        return state.isAir() || BlockType.is(state.getBlock(),
              MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING,
              MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY,
              MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY);
    }

    @Override
    protected Direction getSide(BlockPos pos) {
        int y = pos.getY();
        if (y == baseY) {
            return Direction.DOWN;
        }
        if (y == topY) {
            return Direction.UP;
        }
        if (pos.getX() == minX + 1) {
            return Direction.WEST;
        }
        if (pos.getX() == minX + size - 2) {
            return Direction.EAST;
        }
        if (pos.getZ() == minZ + 1) {
            return Direction.NORTH;
        }
        if (pos.getZ() == minZ + size - 2) {
            return Direction.SOUTH;
        }
        return Direction.NORTH;
    }

    @Override
    public FormationResult postcheck(MeknucFbrMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        int interiorX = minX + 2;
        int interiorZ = minZ + 2;
        int width = size - 4;
        int controlY = topY - 1;
        Set<Long> fuelColumns = new HashSet<>();
        Set<BlockPos> fuelBlocks = new HashSet<>();
        int surfaceArea = 0;
        int fuelAssemblies = 0;
        for (BlockPos pos : structure.internalLocations) {
            int y = pos.getY();
            if (y <= baseY || y >= topY) {
                continue;
            }
            int rx = pos.getX() - interiorX;
            int rz = pos.getZ() - interiorZ;
            if (rx < 0 || rz < 0 || rx >= width || rz >= width) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (rx == 0 || rz == 0 || rx == width - 1 || rz == width - 1) {
                if (!BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING)) {
                    return FormationResult.fail(MeknucFbrLang.INVALID_REFLECTOR, pos);
                }
                continue;
            }
            int cx = rx - 1;
            int cz = rz - 1;
            if (((cx + cz) & 1) != 0) {
                if (!state.isAir()) {
                    return FormationResult.fail(MeknucFbrLang.INVALID_FUEL_SPACING, pos);
                }
                continue;
            }
            if (y == controlY) {
                if (!BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY)) {
                    return FormationResult.fail(MeknucFbrLang.INVALID_MISSING_CONTROL_ROD, pos);
                }
                continue;
            }
            if (!BlockType.is(block, MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY)) {
                return FormationResult.fail(MeknucFbrLang.INVALID_MISSING_FUEL, pos);
            }
            fuelColumns.add(columnKey(pos.getX(), pos.getZ()));
            fuelAssemblies++;
            surfaceArea += 6;
            for (Direction side : EnumUtils.DIRECTIONS) {
                if (fuelBlocks.contains(pos.relative(side))) {
                    surfaceArea -= 2;
                }
            }
            fuelBlocks.add(pos);
        }
        if (fuelColumns.isEmpty()) {
            return FormationResult.fail(MeknucFbrLang.INVALID_MISSING_FUEL);
        }
        int layers = topY - baseY - 1;
        structure.setFuelAssemblies(fuelAssemblies);
        structure.setSurfaceArea(surfaceArea);
        structure.setVolume(size * size * (layers + 2));
        structure.setCladdingCount(layers * (4 * (size - 4) - 4));
        return FormationResult.SUCCESS;
    }

    private static boolean isReactorGlass(Block block) {
        if (reactorGlass == null) {
            reactorGlass = BuiltInRegistries.BLOCK.get(
                  ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "reactor_glass"));
        }
        return reactorGlass != null && block == reactorGlass;
    }

    private static long columnKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
