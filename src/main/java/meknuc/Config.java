package meknuc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue UNSTABLE_EXPLOSION = BUILDER
          .translation("config.meknuc.unstable_explosion")
          .comment("Unstable Explosion: If enabled, an RTG with full storage and no fuel has a chance to explode each second (twice the power of TNT). Chance is configurable below. Default: false")
          .define("unstableExplosion", false);

    public static final ModConfigSpec.DoubleValue UNSTABLE_EXPLOSION_CHANCE = BUILDER
          .translation("config.meknuc.unstable_explosion_chance")
          .comment("Unstable Explosion Chance: chance per second for the RTG to explode when storage is full and there is no fuel (0-1). Default: 0.1")
          .defineInRange("unstableExplosionChance", 0.1D, 0.0D, 1.0D);

    // ---------------------------------------------------------------- 背包辐射

    /** 是否启用“放射性物品放在玩家物品栏里就会产生辐射”这一机制。 */
    public static final ModConfigSpec.BooleanValue INVENTORY_RADIATION_ENABLED;

    /** 镅-241 粉：880 mSv。 */
    public static final ModConfigSpec.DoubleValue AMERICIUM_241_DUST_RADIATION;
    /** 钚-239 粉：23 mSv。 */
    public static final ModConfigSpec.DoubleValue PLUTONIUM_239_DUST_RADIATION;
    /** 铀-233 粉：10 mSv。 */
    public static final ModConfigSpec.DoubleValue URANIUM_233_DUST_RADIATION;
    /** 铀-235 粉：1 μSv。 */
    public static final ModConfigSpec.DoubleValue URANIUM_235_DUST_RADIATION;
    /** 钋-210 粉：1 μSv。 */
    public static final ModConfigSpec.DoubleValue POLONIUM_210_DUST_RADIATION;

    /** 枯竭（非热）燃料棒每个物品格每 tick 施加的剂量（Sv）。 */
    public static final ModConfigSpec.DoubleValue DEPLETED_FUEL_ROD_RADIATION;
    /** 枯竭的热燃料棒每个物品格每 tick 施加的剂量（Sv）。 */
    public static final ModConfigSpec.DoubleValue HOT_DEPLETED_FUEL_ROD_RADIATION;

    // ---------------------------------------------------------------- 销毁爆炸

    /** 放射性物品被销毁（岩浆、仙人掌、燃烧、爆炸、5 分钟超时清理等）时，原地爆炸的威力。 */
    public static final ModConfigSpec.DoubleValue DETONATION_EXPLOSION_POWER;
    /** 爆炸后原地生成的辐射源强度（Sv/h，Mekanism 的源强单位）。 */
    public static final ModConfigSpec.DoubleValue DETONATION_RADIATION;

    static {
        BUILDER.comment("背包内放射性物品产生的辐射").push("radiation");

        INVENTORY_RADIATION_ENABLED = BUILDER
              .comment("是否启用背包放射性物品的辐射。关闭后，所有此类物品放在物品栏内都不再产生辐射。")
              .define("inventoryRadiationEnabled", true);

        BUILDER.comment("每种放射性粉尘每个物品格每 tick 施加的剂量。单位 Sv，20 tick = 1 秒。")
              .comment("剂量与物品数量无关：一摞粉尘也只算一次，分占多个物品格则叠加。")
              .push("dust");

        AMERICIUM_241_DUST_RADIATION = BUILDER
              .comment("镅-241 粉：880 mSv/tick（= 0.88 Sv/tick ≈ 17.6 Sv/s，不带防护约 1 秒致死）。")
              .defineInRange("americium241", 0.88D, 0.0D, 100.0D);

        PLUTONIUM_239_DUST_RADIATION = BUILDER
              .comment("钚-239 粉：23 mSv/tick（= 0.023 Sv/tick ≈ 0.46 Sv/s）。")
              .defineInRange("plutonium239", 0.023D, 0.0D, 100.0D);

        URANIUM_233_DUST_RADIATION = BUILDER
              .comment("铀-233 粉：10 mSv/tick（= 0.01 Sv/tick ≈ 0.2 Sv/s）。")
              .defineInRange("uranium233", 0.01D, 0.0D, 100.0D);

        URANIUM_235_DUST_RADIATION = BUILDER
              .comment("铀-235 粉：1 μSv/tick（= 1.0E-6 Sv/tick ≈ 2.0E-5 Sv/s）。")
              .defineInRange("uranium235", 1.0E-6D, 0.0D, 100.0D);

        POLONIUM_210_DUST_RADIATION = BUILDER
              .comment("钋-210 粉：1 μSv/tick（= 1.0E-6 Sv/tick ≈ 2.0E-5 Sv/s）。")
              .defineInRange("polonium210", 1.0E-6D, 0.0D, 100.0D);

        BUILDER.pop();

        DEPLETED_FUEL_ROD_RADIATION = BUILDER
              .comment("枯竭（非热）燃料棒每个物品格每 tick 施加的辐射剂量，单位 Sv。")
              .defineInRange("depletedFuelRodRadiation", 1.0E-5D, 0.0D, 100.0D);

        HOT_DEPLETED_FUEL_ROD_RADIATION = BUILDER
              .comment("枯竭的热燃料棒每个物品格每 tick 施加的辐射剂量，单位 Sv。")
              .defineInRange("hotDepletedFuelRodRadiation", 5.0E-5D, 0.0D, 100.0D);

        DETONATION_EXPLOSION_POWER = BUILDER
              .comment("放射性物品被销毁时原地爆炸的威力（原版 TNT = 4.0）。")
              .defineInRange("detonationExplosionPower", 3.0D, 0.0D, 16.0D);

        DETONATION_RADIATION = BUILDER
              .comment("爆炸后原地生成的辐射源强度，单位 Sv/h（Mekanism 的源强单位，不是一次性剂量）。")
              .comment("只设中心点、不设半径：Mekanism 的点源会按 1/d² 向四周自然衰减，没有硬边界。")
              .comment("源强会随 Mekanism 的源衰减速率（radiationSourceDecayRate）缓慢衰减。")
              .defineInRange("detonationRadiation", 100.0D, 0.0D, 100000.0D);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
