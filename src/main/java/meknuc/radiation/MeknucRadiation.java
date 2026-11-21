package meknuc.radiation;

import mekanism.api.radiation.IRadiationManager;
import meknuc.Config;
import meknuc.items.MeknucItemRadioactive;
import meknuc.modules.MeknucModules;
import meknuc.modules.MeknucSuitModules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 放射性物品的辐射与“销毁爆炸”逻辑。
 * <p>
 * 物品每 tick 调用一次 {@link #radiateFromInventory(Level, Entity, double)}，实际写入交给 Mekanism 的
 * {@link IRadiationManager#radiate(net.minecraft.world.entity.LivingEntity, double)}，因此：
 * <ul>
 *     <li>防护判定直接复用 Mekanism 的辐射屏蔽：全套防辐射服（四件屏蔽值 0.25 + 0.4 + 0.2 + 0.15 = 1.0）、
 *         以及装够辐射屏蔽单元的 MekaSuit 都会把剂量削减到 0，即完全免疫；</li>
 *     <li>创造 / 旁观模式，以及关闭 Mekanism 全局辐射时不会累积剂量；</li>
 *     <li>剂量与 Mekanism 自己的辐射来源共用同一套衰减、效应与伤害结算。</li>
 * </ul>
 * 本模组的「辐射隔离单元」不提供辐射屏蔽值，所以在这里单独判断：穿齐全套 MekaSuit 且安装了该单元时，
 * 不再从背包累积剂量（已累积的剂量由 {@link MeknucRadiationEvents} 持续清除）。
 */
public final class MeknucRadiation {

    /** 防止一次爆炸把周围的放射性物品逐个引爆，造成无限连锁。 */
    private static boolean detonating = false;

    /**
     * 给持有者施加一次背包辐射。
     *
     * @param level     物品所在世界
     * @param entity    持有物品的实体
     * @param magnitude 本 tick 应施加的剂量（Sv）；单位与 Mekanism 的辐射剂量一致
     */
    public static void radiateFromInventory(Level level, Entity entity, double magnitude) {
        if (magnitude <= 0.0D || level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }
        if (!Config.INVENTORY_RADIATION_ENABLED.get()) {
            return;
        }
        if (isIsolationUnitActive(player)) {
            return;
        }
        IRadiationManager.INSTANCE.radiate(player, magnitude);
    }

    /**
     * 放射性物品实体被销毁时：原地来一发小爆炸，并生成一个中心 100 Sv/h 的辐射源。
     * <p>
     * 由 {@link net.minecraft.world.item.Item#onDestroyed(ItemEntity, net.minecraft.world.damagesource.DamageSource)}
     * （岩浆 / 仙人掌 / 燃烧 / 爆炸等伤害销毁）和 {@link MeknucRadiationEvents}（原版 5 分钟超时清理）调用。
     */
    public static void detonate(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        if (level.isClientSide() || detonating) {
            return;
        }
        detonating = true;
        try {
            double x = itemEntity.getX();
            double y = itemEntity.getY();
            double z = itemEntity.getZ();
            float power = Config.DETONATION_EXPLOSION_POWER.get().floatValue();
            if (power > 0.0F) {
                level.explode(null, x, y, z, power, Level.ExplosionInteraction.BLOCK);
            }
            double radiation = Config.DETONATION_RADIATION.get();
            if (radiation > 0.0D) {
                // Mekanism 的点源：中心为该强度，向四周按 1/d² 衰减，源强本身也会随时间缓慢衰减
                IRadiationManager.INSTANCE.radiate(level, BlockPos.containing(x, y, z), radiation);
            }
        } finally {
            detonating = false;
        }
    }

    /**
     * 该物品堆是否有放射性（粉尘类恒为真，燃料棒仅枯竭状态为真）。
     */
    public static boolean isRadioactive(ItemStack stack) {
        return stack.getItem() instanceof MeknucItemRadioactive radioactive && radioactive.getRadiationPerTick(stack) > 0.0D;
    }

    /**
     * 玩家是否穿齐全套 MekaSuit 并且安装了至少一个已启用的「辐射隔离单元」。
     */
    public static boolean isIsolationUnitActive(Player player) {
        return MeknucSuitModules.isWearingFullSuit(player)
              && MeknucSuitModules.installedCount(player, MeknucModules.RADIATION_ISOLATION) > 0;
    }

    private MeknucRadiation() {
    }
}
