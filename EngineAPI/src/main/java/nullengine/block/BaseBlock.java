package nullengine.block;

import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import nullengine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BaseBlock extends RegistryEntry.Impl<Block> implements Block {

    private final ComponentContainer components = new ComponentContainer();

    private BlockShape shape = BlockShape.NORMAL_CUBE;

    @Nonnull
    @Override
    public BlockShape getShape() {
        return shape;
    }

    @Nonnull
    public BaseBlock setShape(@Nonnull BlockShape shape) {
        this.shape = Objects.requireNonNull(shape);
        return this;
    }

    @Override
    public <T extends Component> Block addComponent(@Nonnull Class<T> type, @Nullable T value) {
        setComponent(type, value);
        return this;
    }

    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return components.getComponent(type);
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        components.setComponent(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.removeComponent(type);
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
