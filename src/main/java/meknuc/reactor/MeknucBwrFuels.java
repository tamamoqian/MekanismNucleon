package meknuc.reactor;

import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.items.MeknucRodType.ReactorKind;
import net.minecraft.world.item.ItemStack;

public class MeknucBwrFuels {

    public static MeknucRodType get(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MeknucItemFuelRodBase)) {
            return null;
        }
        MeknucRodType type = MeknucItemFuelRodBase.getRodType(stack);
        return type.burnTime() > 0 && type.supports(ReactorKind.BWR) && MeknucItemFuelRodBase.isFull(stack)
              ? type : null;
    }

    public static boolean isFuel(ItemStack stack) {
        return get(stack) != null;
    }

    private MeknucBwrFuels() {
    }
}
