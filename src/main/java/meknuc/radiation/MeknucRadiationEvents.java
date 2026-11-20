package meknuc.radiation;

import mekanism.api.radiation.IRadiationManager;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 辐射相关的每 tick / 事件处理：
 * <ul>
 *     <li>原版掉落物 5 分钟超时清理不会触发 {@code Item#onDestroyed}，这里监听 {@link ItemExpireEvent}
 *         补上销毁爆炸；</li>
 *     <li>穿齐全套 MekaSuit 且装有「辐射隔离单元」时，持续清除玩家已累积的辐射剂量，做到真正的隔离。</li>
 * </ul>
 */
public class MeknucRadiationEvents {

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        ItemEntity itemEntity = event.getEntity();
        if (MeknucRadiation.isRadioactive(itemEntity.getItem())) {
            MeknucRadiation.detonate(itemEntity);
        }
    }

    /** 放在 LOWEST：确保在 Mekanism 自己的曝光/伤害结算之后再清零，避免同一 tick 内又被加上剂量。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !MeknucRadiation.isIsolationUnitActive(player)) {
            return;
        }
        IRadiationEntity radiation = player.getCapability(Capabilities.RADIATION_ENTITY);
        if (radiation == null) {
            return;
        }
        double baseline = IRadiationManager.INSTANCE.baselineRadiation();
        if (radiation.getRadiation() > baseline) {
            radiation.set(baseline);
        }
    }
}
