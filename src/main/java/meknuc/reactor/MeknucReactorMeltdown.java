package meknuc.reactor;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mekanism.api.radiation.IRadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MeknucReactorMeltdown {

    public static void destroyStructure(Level world, Explosion explosion, Set<BlockPos> locations,
          Set<BlockPos> internalLocations) {
        List<BlockPos> targets = new ArrayList<>();
        for (BlockPos pos : locations) {
            if (world.random.nextBoolean()) {
                targets.add(pos);
            }
        }
        for (BlockPos pos : internalLocations) {
            if (world.random.nextBoolean()) {
                targets.add(pos);
            }
        }
        List<Pair<ItemStack, BlockPos>> drops = new ArrayList<>();
        for (BlockPos pos : targets) {
            if (!world.isLoaded(pos)) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            state.onExplosionHit(world, pos, explosion, (stack, dropPos) -> addDrop(drops, stack, dropPos));
        }
        for (Pair<ItemStack, BlockPos> pair : drops) {
            Block.popResource(world, pair.getSecond(), pair.getFirst());
        }
    }

    private static void addDrop(List<Pair<ItemStack, BlockPos>> drops, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty()) {
            return;
        }
        for (Pair<ItemStack, BlockPos> existing : drops) {
            if (existing.getSecond().equals(pos)) {
                existing.getFirst().grow(stack.getCount());
                return;
            }
        }
        drops.add(Pair.of(stack, pos));
    }

    public static void radiate(Level world, double centerX, double centerY, double centerZ) {
        if (!IRadiationManager.INSTANCE.isRadiationEnabled()) {
            return;
        }
        int centerChunkX = Mth.floor(centerX) >> 4;
        int centerChunkZ = Mth.floor(centerZ) >> 4;
        int chunkRadius = MeknucReactorConstants.MELTDOWN_RADIATION_RADIUS / 16 + 1;
        int sourceY = Mth.floor(centerY);
        for (int offsetX = -chunkRadius; offsetX <= chunkRadius; offsetX++) {
            for (int offsetZ = -chunkRadius; offsetZ <= chunkRadius; offsetZ++) {
                int sourceX = ((centerChunkX + offsetX) << 4) + 8;
                int sourceZ = ((centerChunkZ + offsetZ) << 4) + 8;
                double deltaX = sourceX - centerX;
                double deltaZ = sourceZ - centerZ;
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (distance >= MeknucReactorConstants.MELTDOWN_RADIATION_RADIUS) {
                    continue;
                }
                double magnitude = MeknucReactorConstants.MELTDOWN_RADIATION_MAGNITUDE
                      * (1.0 - distance / MeknucReactorConstants.MELTDOWN_RADIATION_RADIUS);
                if (magnitude <= 0.0) {
                    continue;
                }
                IRadiationManager.INSTANCE.radiate(world, new BlockPos(sourceX, sourceY, sourceZ), magnitude);
            }
        }
    }

    public static class MeltdownExplosion extends Explosion {

        public MeltdownExplosion(Level world, double x, double y, double z, float radius,
              Explosion.BlockInteraction mode) {
            super(world, null, x, y, z, radius, false, mode);
        }
    }
}
