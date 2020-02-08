package engine.block;

import engine.component.Component;
import engine.component.ComponentAgent;
import engine.registry.Registrable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BaseBlock extends Registrable.Impl<Block> implements Block {

    private final ComponentAgent components = new ComponentAgent();

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
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return components.getComponent(type);
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return components.hasComponent(type);
    }

    @Override
    public <C extends Component> Block setComponent(@Nonnull Class<C> type, @Nullable C value) {
        components.setComponent(type, value);
        return this;
    }

    @Override
    public <C extends Component> Block removeComponent(@Nonnull Class<C> type) {
        components.removeComponent(type);
        return this;
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
