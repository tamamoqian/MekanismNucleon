package meknuc.items;

import java.util.function.DoubleSupplier;

import meknuc.radiation.MeknucRadiation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 放在玩家物品栏内就会持续产生辐射的物品基类。
 * <p>
 * 每个物品格每 tick 施加一次剂量（单位 Sv）：剂量与物品数量无关（一摞只算一次），但分占多个物品格会叠加，
 * 例如背包里 3 格各放一摞粉尘 = 3 倍剂量。防护、配置与剂量写入见 {@link MeknucRadiation}。
 * <p>
 * 另外，这类物品作为掉落物被销毁（岩浆、仙人掌、燃烧、爆炸、5 分钟超时清理等）时，会在原地爆炸并留下辐射区；
 * 见 {@link MeknucRadiation#detonate(ItemEntity)}。
 */
public class MeknucItemRadioactive extends MeknucItemBase {

    private final DoubleSupplier radiationPerTick;

    public MeknucItemRadioactive(Properties properties) {
        this(properties, () -> 0.0D);
    }

    /**
     * @param radiationPerTick 每个物品格每 tick 施加的剂量（Sv），用 {@link DoubleSupplier} 以便实时读取配置
     */
    public MeknucItemRadioactive(Properties properties, DoubleSupplier radiationPerTick) {
        super(properties);
        this.radiationPerTick = radiationPerTick;
    }

    /**
     * 该物品堆每个物品格每 tick 施加的辐射剂量（Sv）。返回 0 表示在物品栏内不产生辐射、销毁时也不爆炸。
     */
    public double getRadiationPerTick(ItemStack stack) {
        return radiationPerTick.getAsDouble();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        double magnitude = getRadiationPerTick(stack);
        if (magnitude > 0.0D) {
            MeknucRadiation.radiateFromInventory(level, entity, magnitude);
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    /**
     * 岩浆 / 仙人掌 / 燃烧 / 爆炸等伤害导致掉落物被销毁时由 NeoForge 调用；
     * 5 分钟超时清理由 {@link meknuc.radiation.MeknucRadiationEvents} 监听 {@code ItemExpireEvent} 处理。
     */
    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (getRadiationPerTick(itemEntity.getItem()) > 0.0D) {
            MeknucRadiation.detonate(itemEntity);
        }
    }
}
