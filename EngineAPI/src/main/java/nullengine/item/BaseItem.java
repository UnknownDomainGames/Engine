package nullengine.item;

import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import nullengine.registry.Registrable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class BaseItem extends Registrable.Impl<Item> implements Item {
    private final ComponentContainer components = new ComponentContainer();

    @Nonnull
    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return components.getComponent(type);
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return components.hasComponent(type);
    }

    @Override
    public <C extends Component> Item setComponent(@Nonnull Class<C> type, @Nullable C value) {
        components.setComponent(type, value);
        return this;
    }

    @Override
    public <C extends Component> Item removeComponent(@Nonnull Class<C> type) {
        components.removeComponent(type);
        return this;
    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }

}
