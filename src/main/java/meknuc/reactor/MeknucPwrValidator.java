package meknuc.reactor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.FormationProtocol.CasingType;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.FormationProtocol.StructureRequirement;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.WorldUtils;
import meknuc.reactor.tile.TileEntityPwrControlAssembly;
import meknuc.reactor.tile.TileEntityPwrFuelAssembly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class MeknucPwrValidator extends CuboidStructureValidator<MeknucPwrMultiblockData> {

    public static final int DIAMETER = 7;
    public static final int MIN_HEIGHT = 7;
    public static final int MAX_HEIGHT = 20;

    private static final double RADIUS_SQ = (DIAMETER / 2.0) * (DIAMETER / 2.0);

    private static Block reactorGlass;

    private final Set<BlockPos> shell = new HashSet<>();
    private VoxelCuboid bounds;
    private int centerX;
    private int centerZ;

    @Override
    public boolean precheck() {
        shell.clear();
        bounds = null;
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
        if (sizeX != DIAMETER || sizeZ != DIAMETER) {
            return false;
        }
        if (sizeY < MIN_HEIGHT || sizeY > MAX_HEIGHT) {
            return false;
        }
        centerX = (minX + maxX) / 2;
        centerZ = (minZ + maxZ) / 2;
        bounds = new VoxelCuboid(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
        cuboid = bounds;
        return true;
    }

    @Override
    public void loadCuboid(VoxelCuboid cuboid) {
        super.loadCuboid(cuboid);
        bounds = cuboid;
        centerX = (cuboid.getMinPos().getX() + cuboid.getMaxPos().getX()) / 2;
        centerZ = (cuboid.getMinPos().getZ() + cuboid.getMaxPos().getZ()) / 2;
    }

    @Override
    protected FormationResult validateNode(FormationProtocol<MeknucPwrMultiblockData> ctx, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        if (!inCircle(pos.getX(), pos.getZ())) {
            return FormationResult.SUCCESS;
        }
        return super.validateNode(ctx, chunkMap, pos);
    }

    @Override
    protected StructureRequirement getStructureRequirement(BlockPos pos) {
        int y = pos.getY();
        boolean ring = isRing(pos.getX(), pos.getZ());
        int bottomY = bounds.getMinPos().getY();
        int topY = bounds.getMaxPos().getY();
        if ((y == bottomY || y == topY) && ring) {
            return StructureRequirement.FRAME;
        }
        if (y == bottomY || y == topY || ring) {
            return StructureRequirement.OTHER;
        }
        return StructureRequirement.INNER;
    }

    @Override
    protected CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (BlockType.is(block, MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_CASING)) {
            return CasingType.FRAME;
        }
        if (BlockType.is(block, MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_PORT,
              MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER)) {
            return CasingType.VALVE;
        }
        if (isReactorGlass(block)) {
            return CasingType.OTHER;
        }
        return CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        return state.isAir() || BlockType.is(state.getBlock(),
              MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
              MeknucPwrBlockTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY);
    }

    @Override
    protected Direction getSide(BlockPos pos) {
        return outwardSide(pos);
    }

    @Override
    public FormationResult postcheck(MeknucPwrMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        Map<Long, Column> byColumn = new HashMap<>();
        for (BlockPos pos : structure.internalLocations) {
            Column column = byColumn.computeIfAbsent(columnKey(pos.getX(), pos.getZ()), key -> new Column());
            BlockEntity tile = WorldUtils.getTileEntity(world, chunkMap, pos);
            if (tile instanceof TileEntityPwrFuelAssembly) {
                column.fuel.add(pos);
            } else if (tile instanceof TileEntityPwrControlAssembly) {
                if (column.controlRod != null) {
                    return FormationResult.fail(MeknucPwrLang.INVALID_EXTRA_CONTROL_ROD, pos);
                }
                column.controlRod = pos;
            }
        }
        if (byColumn.isEmpty()) {
            return FormationResult.fail(MeknucPwrLang.INVALID_MISSING_FUEL);
        }

        List<MeknucPwrMultiblockData.FuelColumn> columns = new ArrayList<>();
        for (Map.Entry<Long, Column> entry : byColumn.entrySet()) {
            Column column = entry.getValue();
            if (column.fuel.isEmpty() && column.controlRod == null) {
                continue;
            }
            if (column.fuel.isEmpty()) {
                return FormationResult.fail(MeknucPwrLang.INVALID_BAD_FUEL_ASSEMBLY, column.controlRod);
            }
            List<BlockPos> fuel = new ArrayList<>(column.fuel);
            fuel.sort(Comparator.comparingInt(BlockPos::getY));
            BlockPos base = fuel.get(0);
            BlockPos top = fuel.get(fuel.size() - 1);
            if (column.controlRod == null) {
                return FormationResult.fail(MeknucPwrLang.INVALID_MISSING_CONTROL_ROD.translateColored(EnumColor.GRAY,
                      EnumColor.INDIGO, MekanismLang.GENERIC_PARENTHESIS.translate(
                            MekanismLang.GENERIC_WITH_COMMA.translate(base.getX(), base.getZ()))));
            }
            for (int i = 1; i < fuel.size(); i++) {
                if (fuel.get(i).getY() != fuel.get(i - 1).getY() + 1) {
                    return FormationResult.fail(MeknucPwrLang.INVALID_MALFORMED_FUEL_ASSEMBLY, fuel.get(i));
                }
            }
            if (column.controlRod.getY() != top.getY() + 1) {
                return FormationResult.fail(MeknucPwrLang.INVALID_BAD_CONTROL_ROD, column.controlRod);
            }
            columns.add(new MeknucPwrMultiblockData.FuelColumn(base, fuel.size(), column.controlRod));
        }

        structure.setFuelColumns(columns);
        return FormationResult.SUCCESS;
    }

    private static class Column {

        private final List<BlockPos> fuel = new ArrayList<>();
        private BlockPos controlRod;
    }

    private static boolean isReactorGlass(Block block) {
        if (reactorGlass == null) {
            reactorGlass = BuiltInRegistries.BLOCK.get(
                  ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "reactor_glass"));
        }
        return reactorGlass != null && block == reactorGlass;
    }

    private boolean inCircle(int x, int z) {
        int dx = x - centerX;
        int dz = z - centerZ;
        return dx * dx + dz * dz <= RADIUS_SQ;
    }

    private boolean isRing(int x, int z) {
        if (!inCircle(x, z)) {
            return false;
        }
        return !inCircle(x - 1, z) || !inCircle(x + 1, z) || !inCircle(x, z - 1) || !inCircle(x, z + 1);
    }

    private Direction outwardSide(BlockPos pos) {
        int y = pos.getY();
        if (y == bounds.getMinPos().getY()) {
            return Direction.DOWN;
        }
        if (y == bounds.getMaxPos().getY()) {
            return Direction.UP;
        }
        int dx = pos.getX() - centerX;
        int dz = pos.getZ() - centerZ;
        if (Math.abs(dx) >= Math.abs(dz)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static long columnKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
