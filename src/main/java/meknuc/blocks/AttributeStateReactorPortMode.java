package meknuc.blocks;

import java.util.List;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeStateReactorPortMode<T extends Enum<T> & StringRepresentable & IHasEnumNameTextComponent> implements AttributeState {

    private final EnumProperty<T> modeProperty;
    private final T defaultMode;

    public AttributeStateReactorPortMode(EnumProperty<T> modeProperty, T defaultMode) {
        this.modeProperty = modeProperty;
        this.defaultMode = defaultMode;
    }

    public EnumProperty<T> getModeProperty() {
        return modeProperty;
    }

    public T getMode(BlockState state) {
        return state.getValue(modeProperty);
    }

    public Component getModeName(BlockState state) {
        return state.getValue(modeProperty).getTextComponent();
    }

    public BlockState nextMode(BlockState state) {
        T current = state.getValue(modeProperty);
        T[] values = current.getDeclaringClass().getEnumConstants();
        return state.setValue(modeProperty, values[(current.ordinal() + 1) % values.length]);
    }

    @Override
    public BlockState copyStateData(BlockState oldState, BlockState newState) {
        AttributeStateReactorPortMode<?> attribute = get(newState);
        if (attribute != null && attribute.modeProperty == modeProperty) {
            newState = newState.setValue(modeProperty, oldState.getValue(modeProperty));
        }
        return newState;
    }

    @Override
    public BlockState getDefaultState(@NotNull BlockState state) {
        return state.setValue(modeProperty, defaultMode);
    }

    @Override
    public void fillBlockStateContainer(Block block, List<Property<?>> properties) {
        properties.add(modeProperty);
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static AttributeStateReactorPortMode<?> get(BlockState state) {
        return (AttributeStateReactorPortMode<?>) Attribute.get(state, (Class) AttributeStateReactorPortMode.class);
    }
}
