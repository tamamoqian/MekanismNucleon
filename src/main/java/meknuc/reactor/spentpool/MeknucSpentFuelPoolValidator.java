package meknuc.reactor.spentpool;

import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 乏燃料冷却池成型校验：长方体 3×3×3 ～ 18×18×18。
 * 边缘必须是耐辐射外壳；墙面允许外壳、端口、逻辑适配器、反应堆玻璃；内部为空气。
 */
public class MeknucSpentFuelPoolValidator extends CuboidStructureValidator<MeknucSpentFuelPoolMultiblockData> {

    private static final ResourceLocation REACTOR_GLASS_ID =
          ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "reactor_glass");

    public MeknucSpentFuelPoolValidator() {
        super();
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        // 棱边只允许耐辐射外壳（FRAME），其余方块只能放在墙面
        if (BlockType.is(block, MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_RADIATION_RESISTANT_CASING)) {
            return FormationProtocol.CasingType.FRAME;
        }
        if (BlockType.is(block, MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_PORT,
              MeknucSpentFuelPoolBlockTypes.SPENT_FUEL_POOL_LOGIC_ADAPTER)) {
            return FormationProtocol.CasingType.VALVE;
        }
        if (isReactorGlass(block)) {
            return FormationProtocol.CasingType.OTHER;
        }
        return FormationProtocol.CasingType.INVALID;
    }

    private static boolean isReactorGlass(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).equals(REACTOR_GLASS_ID);
    }
}
