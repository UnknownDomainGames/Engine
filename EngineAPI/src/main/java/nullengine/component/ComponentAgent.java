package nullengine.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ComponentAgent implements GameObject<ComponentAgent> {

    private final Map<Class<?>, Optional<Component>> components;

    public ComponentAgent() {
        this(new HashMap<>());
    }

    public ComponentAgent(Map<Class<?>, Optional<Component>> components) {
        this.components = components;
    }

    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return (Optional<C>) components.getOrDefault(type, Optional.empty());
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return components.containsKey(type);
    }

    @Override
    public <C extends Component> ComponentAgent setComponent(@Nonnull Class<C> type, @Nullable C value) {
        if (value == null) {
            removeComponent(type);
        } else {
            components.put(type, Optional.of(value));
        }
        return this;
    }

    @Override
    public <T extends Component> ComponentAgent removeComponent(@Nonnull Class<T> type) {
        components.remove(type);
        return this;
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.keySet();
    }
}
