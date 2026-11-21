package meknuc.reactor;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum BwrLogicAdapterMode implements StringRepresentable, IHasEnumNameTextComponent {
    DISABLED("disabled", MeknucBwrLang.LOGIC_DISABLED, MeknucBwrLang.DESCRIPTION_LOGIC_DISABLED,
          new ItemStack(Items.GUNPOWDER), EnumColor.DARK_GRAY),
    ACTIVATION("activation", MeknucBwrLang.LOGIC_ACTIVATION, MeknucBwrLang.DESCRIPTION_LOGIC_ACTIVATION,
          new ItemStack(Items.FLINT_AND_STEEL), EnumColor.AQUA),
    CONTROL_ROD("control_rod", MeknucBwrLang.LOGIC_CONTROL_ROD, MeknucBwrLang.DESCRIPTION_LOGIC_CONTROL_ROD,
          new ItemStack(Items.LEVER), EnumColor.BRIGHT_GREEN),
    HIGH_TEMPERATURE("high_temperature", MeknucBwrLang.LOGIC_HIGH_TEMPERATURE,
          MeknucBwrLang.DESCRIPTION_LOGIC_HIGH_TEMPERATURE, new ItemStack(Items.REDSTONE), EnumColor.RED),
    CRITICAL_WASTE_LEVEL("critical_waste_level", MeknucBwrLang.LOGIC_CRITICAL_WASTE_LEVEL,
          MeknucBwrLang.DESCRIPTION_LOGIC_CRITICAL_WASTE_LEVEL, new ItemStack(Items.REDSTONE), EnumColor.RED),
    DAMAGED("damaged", MeknucBwrLang.LOGIC_DAMAGED, MeknucBwrLang.DESCRIPTION_LOGIC_DAMAGED,
          new ItemStack(Items.REDSTONE), EnumColor.RED);

    public static final BwrLogicAdapterMode[] MODES = values();
    public static final Codec<BwrLogicAdapterMode> CODEC = StringRepresentable.fromEnum(BwrLogicAdapterMode::values);
    public static final IntFunction<BwrLogicAdapterMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BwrLogicAdapterMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final String name;
    private final ILangEntry langEntry;
    private final ILangEntry description;
    private final ItemStack renderStack;
    private final EnumColor color;

    BwrLogicAdapterMode(String name, ILangEntry langEntry, ILangEntry description, ItemStack renderStack,
          EnumColor color) {
        this.name = name;
        this.langEntry = langEntry;
        this.description = description;
        this.renderStack = renderStack;
        this.color = color;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component getTextComponent() {
        return langEntry.translateColored(color);
    }

    public Component getDescription() {
        return description.translate();
    }

    public ItemStack getRenderStack() {
        return renderStack;
    }

    public EnumColor getColor() {
        return color;
    }
}
