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
    public static final DeferredItem<MeknucItem> MEKNUC_ITEM = register("meknuc_item", () -> new MeknucItem(new Item.Properties()));

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
