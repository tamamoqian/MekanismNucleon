package meknuc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
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

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
