package meknuc.modules;

import mekanism.common.registries.MekanismDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 套装升级单元的实际效果处理。
 * <p>
 * 所有效果都在 {@link LivingIncomingDamageEvent} 的最高优先级处理，早于 Mekanism 自身的伤害吸收：
 * <ul>
 *     <li>被减免掉的部分不会消耗装甲能量；</li>
 *     <li>反击按“受到攻击时的伤害”结算，即使该伤害随后被套装完全吸收也依然生效。</li>
 * </ul>
 */
public class MeknucSuitModuleEvents {

    /** 防止反击伤害在两名都安装了反击单元的玩家之间无限递归。 */
    private static boolean reflectingDamage = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getAmount() <= 0.0F) {
            return;
        }
        if (!MeknucSuitModules.isWearingFullSuit(player)) {
            return;
        }
        // 辐射反击单元：最先结算，按“生物对玩家造成的伤害”计算，早于中子/贫铀的处理
        tryReflect(player, event.getSource(), event.getAmount());
        // 中子装甲单元：取消玩家受到的任何伤害判定
        if (MeknucSuitModules.installedCount(player, MeknucModules.NEUTRON_ARMOR) > 0) {
            cancel(event);
            return;
        }
        // 辐射隔离单元：免疫辐射伤害（含背包内放射性物品造成的辐射）
        if (MekanismDamageTypes.RADIATION.is(event.getSource())
              && MeknucSuitModules.installedCount(player, MeknucModules.RADIATION_ISOLATION) > 0) {
            cancel(event);
            return;
        }
        // 贫铀装甲单元：每个单元固定减免 10 点伤害
        int reductionUnits = MeknucSuitModules.installedCount(player, MeknucModules.DEPLETED_URANIUM_ARMOR);
        if (reductionUnits > 0) {
            float remaining = event.getAmount() - ModuleDepletedUraniumArmorUnit.DAMAGE_REDUCTION_PER_UNIT * reductionUnits;
            if (remaining <= 0.0F) {
                cancel(event);
                return;
            }
            event.setAmount(remaining);
        }
    }

    private void tryReflect(Player player, DamageSource source, float damage) {
        if (damage <= 0.0F) {
            return;
        }
        MeknucSuitModules.FoundModule<ModuleRadiationCounterattackUnit> found =
              MeknucSuitModules.findEnabled(player, MeknucModules.RADIATION_COUNTERATTACK);
        if (found == null) {
            return;
        }
        // 仅统计生物对玩家造成的伤害（近战、弹射物等由生物引起的伤害）
        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity) || attacker == player || !attacker.isAlive()) {
            return;
        }
        // 每个单元反弹 50%，按套装能量支付：1 点伤害 = 1 FE
        float ratio = ModuleRadiationCounterattackUnit.REFLECT_RATIO_PER_UNIT * found.module().getInstalledCount();
        float reflected = damage * ratio;
        if (reflected <= 0.0F) {
            return;
        }
        long cost = Math.max(1L, (long) Math.ceil(reflected));
        long spent = found.module().useEnergy(player, found.stack(), cost, false);
        if (spent <= 0L) {
            return;
        }
        reflected *= (float) spent / (float) cost;
        reflectingDamage = true;
        try {
            // 魔法伤害：无视护甲与盔甲韧性
            attacker.hurt(player.damageSources().magic(), reflected);
        } finally {
            reflectingDamage = false;
        }
    }

    private static void cancel(LivingIncomingDamageEvent event) {
        event.setAmount(0.0F);
        event.setCanceled(true);
    }
}
