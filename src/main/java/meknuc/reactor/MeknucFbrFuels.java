package meknuc.reactor;

import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import net.minecraft.world.item.ItemStack;

/**
 * 快中子增殖堆可用的燃料棒：MOX / 锆合金 / 碳化物三种，以及它们对应的耗尽（热）棒。
 *
 * <p>燃烧寿命用文档里的秒数，不走 {@link MeknucRodType#burnTime()}（那套是 PWR/BWR 的 tick 数）。</p>
 */
public final class MeknucFbrFuels {

    public static final MeknucRodType MOX = MeknucRodType.URANIUM_235_MOX;
    public static final MeknucRodType ZIRCALOY = MeknucRodType.URANIUM_235_PLUTONIUM_239_ZIRCALOY;
    public static final MeknucRodType CARBIDE = MeknucRodType.URANIUM_235_PLUTONIUM_239_CARBIDE;

    /** 满功率燃烧寿命（秒）。 */
    public static double burnSeconds(MeknucRodType type) {
        if (type == MOX) {
            return 600.0;
        }
        if (type == ZIRCALOY) {
            return 1000.0;
        }
        if (type == CARBIDE) {
            return 800.0;
        }
        return 600.0;
    }

    /** 满功率堆芯温升（K）。 */
    public static double fullPowerRise(MeknucRodType type) {
        if (type == ZIRCALOY) {
            return 565.0;
        }
        if (type == CARBIDE) {
            return 625.0;
        }
        return 605.0;
    }

    public static MeknucRodType get(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MeknucItemFuelRodBase)) {
            return null;
        }
        if (!MeknucItemFuelRodBase.isFull(stack) || MeknucItemFuelRodBase.isHot(stack)) {
            return null;
        }
        MeknucRodType type = MeknucItemFuelRodBase.getRodType(stack);
        return type == MOX || type == ZIRCALOY || type == CARBIDE ? type : null;
    }

    public static boolean isFuel(ItemStack stack) {
        return get(stack) != null;
    }

    private MeknucFbrFuels() {
    }
}
