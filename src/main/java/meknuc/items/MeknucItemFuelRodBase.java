package meknuc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MeknucItemFuelRodBase extends MeknucItemBase {

    public static final int MAX_RADIATION = 100;

    public MeknucItemFuelRodBase(Properties properties) {
        super(properties);
    }

    public static MeknucRodType getRodType(ItemStack stack) {
        String id = stack.get(MeknucItemBase.ROD_TYPE.get());
        MeknucRodType type = id == null ? null : MeknucRodType.byId(id);
        return type == null ? MeknucRodType.VALUES[0] : type;
    }

    public static void setRodType(ItemStack stack, MeknucRodType type) {
        stack.set(MeknucItemBase.ROD_TYPE.get(), type.id());
    }

    public static int getRadiation(ItemStack stack) {
        return stack.getOrDefault(MeknucItemBase.RADIATION.get(), MAX_RADIATION);
    }

    public static void setRadiation(ItemStack stack, int value) {
        stack.set(MeknucItemBase.RADIATION.get(), value);
    }

    public static boolean isDepleted(ItemStack stack) {
        return getRadiation(stack) <= 0;
    }

    public static boolean isHot(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(MeknucItemBase.ROD_HOT.get()));
    }

    public static void setHot(ItemStack stack, boolean hot) {
        stack.set(MeknucItemBase.ROD_HOT.get(), hot);
    }

    public static ItemStack freshStack(MeknucRodType type, int count) {
        ItemStack stack = new ItemStack(MeknucItemBase.FUEL_ROD.get(), count);
        setRodType(stack, type);
        return stack;
    }

    public static ItemStack depletedStack(MeknucRodType type, int count) {
        ItemStack stack = freshStack(type, count);
        setRadiation(stack, 0);
        return stack;
    }

    public static ItemStack hotStack(MeknucRodType type, int count) {
        ItemStack stack = depletedStack(type, count);
        setHot(stack, true);
        return stack;
    }

    public static float variantValue(ItemStack stack) {
        int state = isHot(stack) ? 2 : isDepleted(stack) ? 1 : 0;
        return getRodType(stack).ordinal() * 3 + state;
    }

    public static void appendTabVariants(CreativeModeTab.Output output) {
        for (MeknucRodType type : MeknucRodType.VALUES) {
            output.accept(freshStack(type, 1));
            output.accept(depletedStack(type, 1));
            if (type.hasHotModel()) {
                output.accept(hotStack(type, 1));
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        MeknucRodType type = getRodType(stack);
        if (isHot(stack)) {
            return Component.translatable("item.meknuc.depleted_hot_" + type.id());
        }
        if (isDepleted(stack)) {
            return Component.translatable("item.meknuc." + type.id() + ".depleted");
        }
        return Component.translatable("item.meknuc." + type.id());
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (isDepleted(stack)) {
            return 13;
        }
        float ratio = Mth.clamp(getRadiation(stack) / (float) MAX_RADIATION, 0.0F, 1.0F);
        return Math.round(ratio * 13.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return isDepleted(stack) ? 0xFF3D3D : 0x7CFC00;
    }

    public static double getPowerPerTick(ItemStack stack) {
        return isDepleted(stack) ? 1.5D : 6.0D;
    }
}
