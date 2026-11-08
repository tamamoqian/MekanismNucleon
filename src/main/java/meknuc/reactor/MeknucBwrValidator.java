package meknuc.reactor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.FormationProtocol.CasingType;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.FormationProtocol.StructureRequirement;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.WorldUtils;
import meknuc.reactor.tile.TileEntityBwrControlAssembly;
import meknuc.reactor.tile.TileEntityBwrFuelAssembly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class MeknucBwrValidator extends CuboidStructureValidator<MeknucBwrMultiblockData> {

    public static final int MIN_DIAMETER = 5;
    public static final int MAX_DIAMETER = 18;
    public static final int MIN_HEIGHT = 5;
    public static final int MAX_HEIGHT = 18;

    private final Set<BlockPos> shell = new HashSet<>();
    private VoxelCuboid bounds;
    private FormationResult shapeFailure;

    @Override
    public boolean precheck() {
        shell.clear();
        bounds = null;
        shapeFailure = null;
        if (structure.getController() == null) {
            return false;
        }
        BlockPos start = structure.getController().getBlockPos();
        List<BlockPos> open = new ArrayList<>();
        open.add(start);
        shell.add(start);
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
        int sizeX = maxX - minX + 1;
        int sizeZ = maxZ - minZ + 1;
        int sizeY = maxY - minY + 1;
        if (sizeX < MIN_DIAMETER || sizeX > MAX_DIAMETER || sizeZ < MIN_DIAMETER || sizeZ > MAX_DIAMETER) {
            shapeFailure = FormationResult.fail(MeknucBwrLang.INVALID_DIAMETER);
            return false;
        }
        if (sizeY < MIN_HEIGHT || sizeY > MAX_HEIGHT) {
            shapeFailure = FormationResult.fail(MeknucBwrLang.INVALID_HEIGHT);
            return false;
        }
        bounds = new VoxelCuboid(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
        cuboid = bounds;
        return true;
    }

    @Override
    public void loadCuboid(VoxelCuboid cuboid) {
        super.loadCuboid(cuboid);
        bounds = cuboid;
    }

    @Override
    public FormationResult validate(FormationProtocol<MeknucBwrMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap) {
        if (!precheck()) {
            return shapeFailure == null ? FormationResult.FAIL : shapeFailure;
        }
        return super.validate(ctx, chunkMap);
    }

    @Override
    protected FormationResult validateNode(FormationProtocol<MeknucBwrMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        if (!inShape(pos.getX(), pos.getY(), pos.getZ())) {
            return FormationResult.SUCCESS;
        }
        return super.validateNode(ctx, chunkMap, pos);
    }

    @Override
    protected StructureRequirement getStructureRequirement(BlockPos pos) {
        if (bounds == null || !inShape(pos.getX(), pos.getY(), pos.getZ())) {
            return StructureRequirement.IGNORED;
        }
        int y = pos.getY();
        boolean middle = y > bounds.getMinPos().getY() && y < bounds.getMaxPos().getY();
        if (middle) {
            return isRing(pos.getX(), pos.getY(), pos.getZ()) ? StructureRequirement.FRAME : StructureRequirement.INNER;
        }
        return isRing(pos.getX(), pos.getY(), pos.getZ()) ? StructureRequirement.FRAME : StructureRequirement.OTHER;
    }

    @Override
    protected CasingType getCasingType(BlockState state) {
        if (BlockType.is(state.getBlock(), MeknucBwrBlockTypes.BOILING_WATER_REACTOR_CASING)) {
            return CasingType.FRAME;
        }
        if (BlockType.is(state.getBlock(), MeknucBwrBlockTypes.BOILING_WATER_REACTOR_PORT,
              MeknucBwrBlockTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER)) {
            return CasingType.VALVE;
        }
        return CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        return state.isAir() || BlockType.is(state.getBlock(),
              MeknucBwrBlockTypes.BOILING_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucBwrBlockTypes.BOILING_WATER_REACTOR_CONTROL_ASSEMBLY);
    }

    @Override
    protected Direction getSide(BlockPos pos) {
        return outwardSide(pos);
    }

    @Override
    public FormationResult postcheck(MeknucBwrMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        Map<Long, Column> byColumn = new HashMap<>();
        Set<BlockPos> fuelBlocks = new HashSet<>();
        int surfaceArea = 0;
        for (BlockPos pos : structure.internalLocations) {
            Column column = byColumn.computeIfAbsent(columnKey(pos.getX(), pos.getZ()), key -> new Column());
            BlockEntity tile = WorldUtils.getTileEntity(world, chunkMap, pos);
            if (tile instanceof TileEntityBwrFuelAssembly) {
                column.fuel.add(pos);
                surfaceArea += 6;
                for (Direction side : EnumUtils.DIRECTIONS) {
                    if (fuelBlocks.contains(pos.relative(side))) {
                        surfaceArea -= 2;
                    }
                }
                fuelBlocks.add(pos);
            } else if (tile instanceof TileEntityBwrControlAssembly) {
                if (column.control != null) {
                    return FormationResult.fail(MeknucBwrLang.INVALID_EXTRA_CONTROL_ROD, pos);
                }
                column.control = pos;
            }
        }
        if (byColumn.isEmpty()) {
            return FormationResult.fail(MeknucBwrLang.INVALID_MISSING_FUEL);
        }

        int baseY = bounds.getMinPos().getY() + 1;
        int topY = bounds.getMaxPos().getY() - 1;
        int assemblies = 0;
        List<MeknucBwrMultiblockData.FuelColumn> columns = new ArrayList<>();
        for (Column column : byColumn.values()) {
            if (column.fuel.isEmpty()) {
                return FormationResult.fail(MeknucBwrLang.INVALID_MISSING_FUEL, column.control);
            }
            List<BlockPos> fuel = new ArrayList<>(column.fuel);
            fuel.sort(Comparator.comparingInt(BlockPos::getY));
            for (int i = 1; i < fuel.size(); i++) {
                if (fuel.get(i).getY() != fuel.get(i - 1).getY() + 1) {
                    return FormationResult.fail(MeknucBwrLang.INVALID_MALFORMED_FUEL_ASSEMBLY, fuel.get(i));
                }
            }
            BlockPos base = fuel.get(0);
            BlockPos top = fuel.get(fuel.size() - 1);
            if (base.getY() != baseY) {
                return FormationResult.fail(MeknucBwrLang.INVALID_BAD_FUEL_ASSEMBLY, base);
            }
            if (column.control == null || column.control.getY() != topY) {
                return FormationResult.fail(MeknucBwrLang.INVALID_BAD_CONTROL_ROD, top);
            }
            if (top.getY() != topY - 1) {
                return FormationResult.fail(MeknucBwrLang.INVALID_BAD_FUEL_ASSEMBLY, top);
            }
            columns.add(new MeknucBwrMultiblockData.FuelColumn(base, fuel.size()));
            assemblies += fuel.size();
        }

        structure.setFuelColumns(columns);
        structure.setFuelAssemblies(assemblies);
        structure.setSurfaceArea(surfaceArea);
        return FormationResult.SUCCESS;
    }

    private static class Column {

        private final List<BlockPos> fuel = new ArrayList<>();
        private BlockPos control;
    }

    private boolean inShape(int x, int y, int z) {
        if (bounds == null) {
            return false;
        }
        int y0 = bounds.getMinPos().getY();
        int y1 = bounds.getMaxPos().getY();
        return y >= y0 && y <= y1 && inLayer(x, y, z);
    }

    private boolean isRing(int x, int y, int z) {
        if (!inLayer(x, y, z)) {
            return false;
        }
        int inset = layerInset(y);
        return x == bounds.getMinPos().getX() + inset || x == bounds.getMaxPos().getX() - inset
              || z == bounds.getMinPos().getZ() + inset || z == bounds.getMaxPos().getZ() - inset;
    }

    private boolean inLayer(int x, int y, int z) {
        if (bounds == null) {
            return false;
        }
        int inset = layerInset(y);
        return x >= bounds.getMinPos().getX() + inset && x <= bounds.getMaxPos().getX() - inset
              && z >= bounds.getMinPos().getZ() + inset && z <= bounds.getMaxPos().getZ() - inset;
    }

    private int layerInset(int y) {
        return y == bounds.getMinPos().getY() || y == bounds.getMaxPos().getY() ? 0 : 1;
    }

    private Direction outwardSide(BlockPos pos) {
        int y = pos.getY();
        if (y == bounds.getMinPos().getY()) {
            return Direction.DOWN;
        }
        if (y == bounds.getMaxPos().getY()) {
            return Direction.UP;
        }
        int x = pos.getX();
        int z = pos.getZ();
        int west = x - bounds.getMinPos().getX() - 1;
        int east = bounds.getMaxPos().getX() - 1 - x;
        int north = z - bounds.getMinPos().getZ() - 1;
        int south = bounds.getMaxPos().getZ() - 1 - z;
        if (Math.min(west, east) <= Math.min(north, south)) {
            return west <= east ? Direction.WEST : Direction.EAST;
        }
        return north <= south ? Direction.NORTH : Direction.SOUTH;
    }

    private static long columnKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
