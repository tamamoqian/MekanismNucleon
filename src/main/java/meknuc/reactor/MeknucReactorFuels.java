package meknuc.reactor;

import java.util.HashMap;
import java.util.Map;
import meknuc.items.MeknucItemBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MeknucReactorFuels {

    private static Map<Item, Spec> fuels;

    public static Spec get(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return fuels().get(stack.getItem());
    }

    public static boolean isFuel(ItemStack stack) {
        return get(stack) != null;
    }

    private static Map<Item, Spec> fuels() {
        if (fuels == null) {
            Map<Item, Spec> map = new HashMap<>();
            add(map, MeknucItemBase.URANIUM_235_MOX_FUEL_ROD.get(), 24000,
                  MeknucItemBase.DEPLETED_HOT_URANIUM_235_MOX_FUEL_ROD.get());
            add(map, MeknucItemBase.URANIUM_235_THORIUM_FUEL_ROD.get(), 30000,
                  MeknucItemBase.DEPLETED_HOT_URANIUM_235_THORIUM_FUEL_ROD.get());
            add(map, MeknucItemBase.URANIUM_235_PLUTONIUM_239_FUEL_ROD.get(), 36000,
                  MeknucItemBase.DEPLETED_HOT_URANIUM_235_PLUTONIUM_239_FUEL_ROD.get());
            fuels = map;
        }
        return fuels;
    }

    private static void add(Map<Item, Spec> map, Item fuel, int burnTime, Item product) {
        map.put(fuel, new Spec(burnTime, product));
    }

    public record Spec(int burnTime, Item product) {
    }

    private MeknucReactorFuels() {
    }
}
