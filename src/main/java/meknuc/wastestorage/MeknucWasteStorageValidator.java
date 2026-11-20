package meknuc.wastestorage;

import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import meknuc.blocks.MeknucWasteStorageBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 放射性废料存储罐的成型校验。
 * <p>
 * 形状：长方体，**底部最大 3 × 3，高度最高 18 格**（最小 1 × 1 × 1）。
 * 外壳只能由「耐辐射废料存储外壳」和「耐辐射废料存储端口」组成，内部必须是空气。
 * <p>
 * 与反应堆不同，这里不强制棱边必须是外壳：底部只有 1～2 格宽时整面都是棱边，
 * 如果还要求棱边必须是外壳，端口就永远放不上去。因此外壳面上的任意一格都可以是外壳或端口，
 * 端口本身仍然按阀门（VALVE）登记，供多方块做自动输出。
 */
public class MeknucWasteStorageValidator extends CuboidStructureValidator<MeknucWasteStorageMultiblockData> {

    /** 最小 1 × 1 × 1。 */
    private static final VoxelCuboid MIN_BOUNDS = new VoxelCuboid(1, 1, 1);
    /** 最大 3 × 18 × 3（长 × 高 × 宽）。 */
    private static final VoxelCuboid MAX_BOUNDS = new VoxelCuboid(3, 18, 3);

    public MeknucWasteStorageValidator() {
        super(MIN_BOUNDS, MAX_BOUNDS);
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (BlockType.is(block, MeknucWasteStorageBlockTypes.RADIATION_RESISTANT_WASTE_STORAGE_CASING)) {
            return FormationProtocol.CasingType.FRAME;
        }
        if (BlockType.is(block, MeknucWasteStorageBlockTypes.RADIATION_RESISTANT_WASTE_STORAGE_PORT)) {
            return FormationProtocol.CasingType.VALVE;
        }
        return FormationProtocol.CasingType.INVALID;
    }

    /**
     * 外壳面（含棱边 / 角落）都只要求「是合法外壳方块」，不强制必须是外壳而不是端口。
     */
    @Override
    protected FormationProtocol.StructureRequirement getStructureRequirement(BlockPos pos) {
        return cuboid.getWallRelative(pos).isWall()
              ? FormationProtocol.StructureRequirement.OTHER
              : FormationProtocol.StructureRequirement.INNER;
    }
}
