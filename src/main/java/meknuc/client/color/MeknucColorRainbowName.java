package meknuc.client.color;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

public final class MeknucColorRainbowName {

    private MeknucColorRainbowName() {
    }

    public static Component rainbowName(Component source, long tick) {
        String text = source.getString();
        if (text.isBlank() || text.startsWith("item.meknuc.")) {
            return source;
        }
        MutableComponent result = Component.empty();
        int chars = text.length();
        for (int i = 0; i < chars; i++) {
            float hue = (tick * 3.0F + i * 40.0F) % 360.0F / 360.0F;
            int rgb = Mth.hsvToRgb(hue, 1.0F, 1.0F);
            result.append(Component.literal(String.valueOf(text.charAt(i)))
                  .withStyle(style -> style.withColor(TextColor.fromRgb(rgb)).withBold(true)));
        }
        return result;
    }
}
