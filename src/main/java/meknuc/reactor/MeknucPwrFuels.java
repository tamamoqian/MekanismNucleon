package meknuc.reactor;

import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.items.MeknucRodType.ReactorKind;
import net.minecraft.world.item.ItemStack;

public class MeknucPwrFuels {

    public static MeknucRodType get(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MeknucItemFuelRodBase)) {
            return null;
        }
        MeknucRodType type = MeknucItemFuelRodBase.getRodType(stack);
        return type.burnTime() > 0 && type.supports(ReactorKind.PWR) && MeknucItemFuelRodBase.isFull(stack)
              ? type : null;
    }

    public static boolean isFuel(ItemStack stack) {
        return get(stack) != null;
    }

    private MeknucPwrFuels() {
    }
}
