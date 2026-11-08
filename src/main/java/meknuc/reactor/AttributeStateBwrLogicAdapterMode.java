package meknuc.reactor;

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

public class AttributeStateBwrLogicAdapterMode<T extends Enum<T> & StringRepresentable & IHasEnumNameTextComponent> implements AttributeState {

    public static final EnumProperty<BwrLogicAdapterMode> modeProperty =
          EnumProperty.create("mode", BwrLogicAdapterMode.class);

    private final EnumProperty<T> property;
    private final T defaultMode;

    public AttributeStateBwrLogicAdapterMode(EnumProperty<T> property, T defaultMode) {
        this.property = property;
        this.defaultMode = defaultMode;
    }

    public EnumProperty<T> getModeProperty() {
        return property;
    }

    public T getMode(BlockState state) {
        return state.getValue(property);
    }

    public Component getModeName(BlockState state) {
        return state.getValue(property).getTextComponent();
    }

    public BlockState nextMode(BlockState state) {
        T current = state.getValue(property);
        T[] values = current.getDeclaringClass().getEnumConstants();
        return state.setValue(property, values[(current.ordinal() + 1) % values.length]);
    }

    @Override
    public BlockState copyStateData(BlockState oldState, BlockState newState) {
        AttributeStateBwrLogicAdapterMode<?> attribute = get(newState);
        if (attribute != null && attribute.property == property) {
            newState = newState.setValue(property, oldState.getValue(property));
        }
        return newState;
    }

    @Override
    public BlockState getDefaultState(@NotNull BlockState state) {
        return state.setValue(property, defaultMode);
    }

    @Override
    public void fillBlockStateContainer(Block block, List<Property<?>> properties) {
        properties.add(property);
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static AttributeStateBwrLogicAdapterMode<?> get(BlockState state) {
        return (AttributeStateBwrLogicAdapterMode<?>) Attribute.get(state, (Class) AttributeStateBwrLogicAdapterMode.class);
    }
}
