package meknuc.items;

import java.util.List;

import meknuc.Config;
import meknuc.client.color.MeknucColorLerp;
import meknuc.client.color.MeknucColorRainbowName;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MeknucItemFuelRodBase extends MeknucItemRadioactive {

    public static final int MAX_RADIATION = 100;

    public MeknucItemFuelRodBase(Properties properties) {
        super(properties);
    }

    public static MeknucRodType getRodType(ItemStack stack) {
        String id = stack.get(MeknucItems.ROD_TYPE.get());
        MeknucRodType type = id == null ? null : MeknucRodType.byId(id);
        return type == null ? MeknucRodType.VALUES[0] : type;
    }

    public static void setRodType(ItemStack stack, MeknucRodType type) {
        stack.set(MeknucItems.ROD_TYPE.get(), type.id());
    }

    public static int getRadiation(ItemStack stack) {
        return stack.getOrDefault(MeknucItems.RADIATION.get(), MAX_RADIATION);
    }

    public static void setRadiation(ItemStack stack, int value) {
        stack.set(MeknucItems.RADIATION.get(), value);
    }

    public static boolean isDepleted(ItemStack stack) {
        return getRadiation(stack) <= 0;
    }

    public static boolean isFull(ItemStack stack) {
        return getRadiation(stack) >= MAX_RADIATION;
    }

    public static boolean isHot(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(MeknucItems.ROD_HOT.get()));
    }

    public static boolean isInfinite(ItemStack stack) {
        return getRodType(stack).isInfinite();
    }

    public static void setHot(ItemStack stack, boolean hot) {
        stack.set(MeknucItems.ROD_HOT.get(), hot);
    }

    public static ItemStack freshStack(MeknucRodType type, int count) {
        ItemStack stack = new ItemStack(MeknucItems.FUEL_ROD.get(), count);
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

    private static final int GRADIENT_FROM = 0x5555FF;
    private static final int GRADIENT_TO = 0xFFFF00;

    @Override
    public Component getName(ItemStack stack) {
        MeknucRodType type = getRodType(stack);
        if (type.isInfinite()) {
            return gradientName(Component.translatable("item.meknuc." + type.id()));
        }
        if (isHot(stack)) {
            return Component.translatable("item.meknuc.depleted_hot_" + type.id());
        }
        if (isDepleted(stack)) {
            return Component.translatable("item.meknuc." + type.id() + ".depleted");
        }
        return Component.translatable("item.meknuc." + type.id());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (getRodType(stack).isInfinite()) {
            Level level = context.level();
            long tick = level == null ? System.currentTimeMillis() / 50L : level.getGameTime();
            tooltip.add(MeknucColorRainbowName.rainbowName(Component.translatable("item.meknuc." + getRodType(stack).id() + ".infinite"), tick));
        }
    }

    public static Component gradientName(Component source) {
        String text = source.getString();
        if (text.isBlank() || text.startsWith("item.meknuc.")) {
            return source;
        }
        MutableComponent result = Component.empty();
        int chars = text.length();
        for (int i = 0; i < chars; i++) {
            float t = chars <= 1 ? 0F : i / (float) (chars - 1);
            result.append(Component.literal(String.valueOf(text.charAt(i)))
                  .withStyle(style -> style.withColor(TextColor.fromRgb(MeknucColorLerp.lerpColor(GRADIENT_FROM, GRADIENT_TO, t)))
                        .withBold(true)));
        }
        return result;
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
        return isDepleted(stack) ? 60.0D : 9600.0D;
    }

    /**
     * 枯竭燃料棒放在玩家物品栏内时每 tick 施加的辐射剂量（Sv）：热的枯竭燃料棒更高；未枯竭的新燃料棒为 0，
     * 既不会在背包里辐射，被销毁时也不会爆炸。
     */
    @Override
    public double getRadiationPerTick(ItemStack stack) {
        if (!isDepleted(stack)) {
            return 0.0D;
        }
        return isHot(stack) ? Config.HOT_DEPLETED_FUEL_ROD_RADIATION.get() : Config.DEPLETED_FUEL_ROD_RADIATION.get();
    }
}
