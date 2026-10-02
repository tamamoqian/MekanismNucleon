package meknuc.reactor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import meknuc.meknuc;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class MeknucReactorFuels {

    public static final ResourceLocation REACTOR_FUEL_ID = ResourceLocation.fromNamespaceAndPath(meknuc.MODID, "reactor_fuel");

    public static final DataMapType<Item, ReactorFuel> REACTOR_FUEL = DataMapType.builder(
          REACTOR_FUEL_ID, Registries.ITEM, ReactorFuel.CODEC).synced(ReactorFuel.CODEC, true).build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(REACTOR_FUEL);
    }

    public static Map<ResourceKey<Item>, ReactorFuel> fuels() {
        return BuiltInRegistries.ITEM.getDataMap(REACTOR_FUEL);
    }

    public static ReactorFuel get(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.getItemHolder().getData(REACTOR_FUEL);
    }

    public static boolean isFuel(ItemStack stack) {
        return get(stack) != null;
    }

    public static ItemStack product(ReactorFuel fuel, int count) {
        return new ItemStack(fuel.product().value(), count);
    }

    public record ReactorFuel(int burnTime, Holder<Item> product) {

        public static final Codec<ReactorFuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
              Codec.INT.fieldOf("burn_time").forGetter(ReactorFuel::burnTime),
              BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("product").forGetter(ReactorFuel::product)
        ).apply(instance, ReactorFuel::new));
    }

    private MeknucReactorFuels() {
    }
}
