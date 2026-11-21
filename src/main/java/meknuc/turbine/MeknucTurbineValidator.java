package meknuc.turbine;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import mekanism.api.text.ILangEntry;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.tile.TileEntityPressureDisperser;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineMultiblockData.VentData;
import mekanism.generators.common.registries.GeneratorsBlockTypes;
import mekanism.generators.common.tile.turbine.TileEntityElectromagneticCoil;
import mekanism.generators.common.tile.turbine.TileEntityRotationalComplex;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCondenser;
import meknuc.turbine.tile.TileEntityHighPressureTurbineVent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * 高压蒸汽涡轮的结构校验器。
 * <p>
 * 与 Mekanism 工业涡轮完全同构：高压外壳框 / 高压端口（阀门）/ 高压排气口 /
 * 复合旋钮装置 / 连续转子柱 / 上层线圈 / 高压冷凝器。分压层继续使用 Mekanism
 * 原本的分压元件（pressure disperser）。
 */
public class MeknucTurbineValidator extends CuboidStructureValidator<TurbineMultiblockData> {

    public MeknucTurbineValidator() {
        super(new VoxelCuboid(5, 3, 5), new VoxelCuboid(17, 18, 17));
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (BlockType.is(block, MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_CASING)) {
            return FormationProtocol.CasingType.FRAME;
        }
        if (BlockType.is(block, MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_PORT)) {
            return FormationProtocol.CasingType.VALVE;
        }
        if (BlockType.is(block, MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_VENT)) {
            return FormationProtocol.CasingType.OTHER;
        }
        return FormationProtocol.CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        if (super.validateInner(state, chunkMap, pos)) {
            return true;
        }
        return BlockType.is(state.getBlock(),
              MekanismBlockTypes.PRESSURE_DISPERSER,
              MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_CONDENSER,
              GeneratorsBlockTypes.TURBINE_ROTOR, GeneratorsBlockTypes.ROTATIONAL_COMPLEX,
              GeneratorsBlockTypes.ELECTROMAGNETIC_COIL);
    }

    @Override
    public FormationProtocol.FormationResult postcheck(TurbineMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        if (structure.length() % 2 != 1 || structure.width() % 2 != 1) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_EVEN_LENGTH);
        }
        int centerX = structure.getMinPos().getX() + (structure.length() - 1) / 2;
        int centerZ = structure.getMinPos().getZ() + (structure.width() - 1) / 2;
        BlockPos complex = null;
        ObjectOpenHashSet<BlockPos> rotors = new ObjectOpenHashSet<>();
        ObjectOpenHashSet<BlockPos> dispersers = new ObjectOpenHashSet<>();
        ObjectOpenHashSet<BlockPos> coils = new ObjectOpenHashSet<>();
        ObjectOpenHashSet<BlockPos> condensers = new ObjectOpenHashSet<>();
        for (BlockPos pos : structure.internalLocations) {
            BlockEntity tile = WorldUtils.getTileEntity((LevelAccessor) this.world, chunkMap, pos);
            if (tile instanceof TileEntityRotationalComplex) {
                if (complex != null || pos.getX() != centerX || pos.getZ() != centerZ) {
                    return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_BAD_COMPLEX, pos);
                }
                complex = pos;
            } else if (tile instanceof TileEntityTurbineRotor) {
                if (pos.getX() != centerX || pos.getZ() != centerZ) {
                    return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_BAD_ROTOR, pos);
                }
                rotors.add(pos);
            } else if (tile instanceof TileEntityPressureDisperser) {
                dispersers.add(pos);
            } else if (tile instanceof TileEntityElectromagneticCoil) {
                coils.add(pos);
            } else if (tile instanceof TileEntityHighPressureTurbineCondenser) {
                condensers.add(pos);
            }
        }
        if (complex == null) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MISSING_COMPLEX);
        }
        int rotorCount = complex.getY() - structure.getMinPos().getY() + 1;
        int innerRadius = (Math.min(structure.length(), structure.width()) - 3) / 2;
        if (innerRadius < rotorCount / 4) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_TOO_NARROW);
        }
        if (coils.isEmpty()) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MISSING_COILS);
        }
        if (dispersers.isEmpty()) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MISSING_DISPERSERS);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int innerRadiusX = (structure.length() - 3) / 2;
        int innerRadiusZ = (structure.width() - 3) / 2;
        for (int x = complex.getX() - innerRadiusX; x <= complex.getX() + innerRadiusX; x++) {
            for (int z = complex.getZ() - innerRadiusZ; z <= complex.getZ() + innerRadiusZ; z++) {
                if (x == centerX && z == centerZ) {
                    continue;
                }
                mutablePos.set(x, complex.getY(), z);
                TileEntityPressureDisperser tile = WorldUtils.getTileEntity(TileEntityPressureDisperser.class,
                      (LevelAccessor) this.world, chunkMap, mutablePos);
                if (tile == null) {
                    return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MISSING_DISPERSER, mutablePos);
                }
                dispersers.remove(mutablePos);
            }
        }
        if (!dispersers.isEmpty()) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MALFORMED_DISPERSERS);
        }
        for (BlockPos coord : condensers) {
            if (coord.getY() > complex.getY()) {
                continue;
            }
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_CONDENSER_BELOW_COMPLEX, coord);
        }
        structure.condensers = condensers.size();
        int turbineHeight = 0;
        int blades = 0;
        for (int y = complex.getY() - 1; y > structure.getMinPos().getY(); y--) {
            mutablePos.set(centerX, y, centerZ);
            TileEntityTurbineRotor rotor = WorldUtils.getTileEntity(TileEntityTurbineRotor.class,
                  (LevelAccessor) this.world, chunkMap, mutablePos);
            if (rotor == null) {
                return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_ROTORS_NOT_CONTIGUOUS);
            }
            turbineHeight++;
            blades += rotor.getHousedBlades();
            rotors.remove(mutablePos);
        }
        if (!rotors.isEmpty()) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_BAD_ROTORS);
        }
        if (blades == 0) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_NO_BLADES);
        }
        structure.blades = blades;
        structure.coils = FormationProtocol.explore((Level) this.world, chunkMap, complex.relative(Direction.UP), null,
              (level, chunks, start, n, pos) -> WorldUtils.getTileEntity(TileEntityElectromagneticCoil.class,
                    (LevelAccessor) level, chunks, pos) != null);
        if (coils.size() > structure.coils) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MALFORMED_COILS);
        }
        ArrayList<VentData> ventData = new ArrayList<>();
        for (BlockPos coord : structure.locations) {
            if (WorldUtils.getTileEntity(TileEntityHighPressureTurbineVent.class, (LevelAccessor) this.world, chunkMap, coord) == null) {
                continue;
            }
            if (coord.getY() < complex.getY()) {
                return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_VENT_BELOW_COMPLEX, coord);
            }
            ventData.add(new VentData(coord, this.getSide(coord)));
        }
        if (ventData.isEmpty()) {
            return FormationProtocol.FormationResult.fail((ILangEntry) GeneratorsLang.TURBINE_INVALID_MISSING_VENTS);
        }
        structure.updateVentData(ventData);
        structure.lowerVolume = structure.length() * structure.width() * turbineHeight;
        structure.complex = complex;
        return FormationProtocol.FormationResult.SUCCESS;
    }
}
