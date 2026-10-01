package meknuc.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import meknuc.meknuc;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MeknucItemBase extends Item {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(meknuc.MODID);
    private static final List<Supplier<ItemStack>> TAB_ITEMS = new ArrayList<>();
    public static final DeferredItem<MeknucItemOreZircon> ZIRCON_ORE_ITEM = register("zircon_ore_item", () -> new MeknucItemOreZircon(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotZircon> ZIRCON_INGOT = register("zircon_ingot", () -> new MeknucItemIngotZircon(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreThorium> THORIUM_ORE_ITEM = register("thorium_ore_item", () -> new MeknucItemOreThorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotThorium> THORIUM_INGOT = register("thorium_ingot", () -> new MeknucItemIngotThorium(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreBeryllium> BERYLLIUM_ORE_ITEM = register("beryllium_ore_item", () -> new MeknucItemOreBeryllium(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotBeryllium> BERYLLIUM_INGOT = register("beryllium_ingot", () -> new MeknucItemIngotBeryllium(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreBoron> BORON_ORE_ITEM = register("boron_ore_item", () -> new MeknucItemOreBoron(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotBoron> BORON_INGOT = register("boron_ingot", () -> new MeknucItemIngotBoron(new Item.Properties()));
    public static final DeferredItem<MeknucItemOreChrome> CHROME_ORE_ITEM = register("chrome_ore_item", () -> new MeknucItemOreChrome(new Item.Properties()));
    public static final DeferredItem<MeknucItemIngotChrome> CHROME_INGOT = register("chrome_ingot", () -> new MeknucItemIngotChrome(new Item.Properties()));

    public MeknucItemBase(Properties properties) {
        super(properties);
    }

    private static <T extends MeknucItemBase> DeferredItem<T> register(String name, Supplier<T> supplier) {
        DeferredItem<T> deferred = ITEMS.register(name, supplier);
        TAB_ITEMS.add(() -> deferred.get().getDefaultInstance());
        return deferred;
    }

    public static void addTabItems(CreativeModeTab.Output output) {
        TAB_ITEMS.forEach(supplier -> output.accept(supplier.get()));
    }
}
