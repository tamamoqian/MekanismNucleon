package meknuc.reactor;

import mekanism.common.content.blocktype.BlockType;
import meknuc.blocks.MeknucFbrBlockTypes;
import meknuc.blocks.MeknucFbrBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 测试用：创造模式下潜行 + 空手右键快中子增殖堆外壳，直接在原地生成一座最小尺寸的可成形反应堆。
 *
 * <p>生成 9×9×5：底面 9×9 全外壳；中间 3 层 7×7（外壳环 + 包壳圈 + 燃料晶格，晶格间隔留空），
 * 每层四个角各补 2 格棱边；顶部下方那一层换成控制组件；顶层 9×9 全外壳。</p>
 */
public final class MeknucFbrTestStructure {

    public static final int SIZE = 9;
    public static final int HEIGHT = 5;

    private static final int HALF = SIZE / 2;

    private MeknucFbrTestStructure() {
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Player player = event.getEntity();
        if (!player.isCreative() || !player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()) {
            return;
        }
        BlockPos clicked = event.getPos();
        BlockState clickedState = level.getBlockState(clicked);
        if (!BlockType.is(clickedState.getBlock(), MeknucFbrBlockTypes.FAST_BREEDER_REACTOR_CASING)) {
            return;
        }
        BlockPos min = clicked.offset(-HALF, 0, -HALF);
        if (min.getY() < level.getMinBuildHeight() || min.getY() + HEIGHT - 1 > level.getMaxBuildHeight() - 1) {
            player.displayClientMessage(Component.translatable("message.meknuc.fbr.test.no_space", SIZE, HEIGHT, SIZE)
                  .withStyle(ChatFormatting.RED), false);
            return;
        }
        build(level, min);
        player.displayClientMessage(Component.translatable("message.meknuc.fbr.test.built", SIZE, HEIGHT)
              .withStyle(ChatFormatting.GREEN), false);
        event.setCanceled(true);
    }

    private static void build(ServerLevel level, BlockPos min) {
        BlockState casing = MeknucFbrBlocks.FAST_BREEDER_REACTOR_CASING.defaultState();
        BlockState cladding = MeknucFbrBlocks.FAST_BREEDER_REACTOR_NEUTRON_REFLECTOR_CLADDING.defaultState();
        BlockState fuel = MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_FUEL_ASSEMBLY.defaultState();
        BlockState control = MeknucFbrBlocks.FAST_BREEDER_REACTOR_RADIATION_RESISTANT_CONTROL_ASSEMBLY.defaultState();
        int controlY = HEIGHT - 2;
        int width = SIZE - 4;
        int lo = 1;
        int hi = SIZE - 2;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = 0; y < HEIGHT; y++) {
            if (y == 0 || y == HEIGHT - 1) {
                for (int x = 0; x < SIZE; x++) {
                    for (int z = 0; z < SIZE; z++) {
                        set(level, cursor, min, x, y, z, casing);
                    }
                }
                continue;
            }
            for (int x = lo; x <= hi; x++) {
                for (int z = lo; z <= hi; z++) {
                    BlockState target;
                    if (x == lo || z == lo || x == hi || z == hi) {
                        target = casing;
                    } else {
                        int rx = x - 2;
                        int rz = z - 2;
                        boolean cladRing = rx == 0 || rz == 0 || rx == width - 1 || rz == width - 1;
                        if (cladRing) {
                            target = cladding;
                        } else if ((((rx - 1) + (rz - 1)) & 1) != 0) {
                            continue;
                        } else {
                            target = y == controlY ? control : fuel;
                        }
                    }
                    set(level, cursor, min, x, y, z, target);
                }
            }
            // 四角各向外伸出 2 格棱边（沿 X、沿 Z 各一格，斜角那格不加）
            int last = SIZE - 1;
            set(level, cursor, min, 0, y, lo, casing);
            set(level, cursor, min, 0, y, hi, casing);
            set(level, cursor, min, last, y, lo, casing);
            set(level, cursor, min, last, y, hi, casing);
            set(level, cursor, min, lo, y, 0, casing);
            set(level, cursor, min, hi, y, 0, casing);
            set(level, cursor, min, lo, y, last, casing);
            set(level, cursor, min, hi, y, last, casing);
        }
        BlockPos anchor = min.offset(HALF, 0, HALF);
        level.blockUpdated(anchor, level.getBlockState(anchor).getBlock());
        BlockPos topCenter = min.offset(HALF, HEIGHT - 1, HALF);
        level.blockUpdated(topCenter, level.getBlockState(topCenter).getBlock());
    }

    private static void set(ServerLevel level, BlockPos.MutableBlockPos cursor, BlockPos min, int x, int y, int z, BlockState state) {
        cursor.set(min.getX() + x, min.getY() + y, min.getZ() + z);
        level.setBlock(cursor, state, Block.UPDATE_ALL);
    }
}
