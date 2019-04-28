package unknowndomain.engine.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ComponentContainer implements GameObject {

    private final Map<Class<?>, Optional<Component>> components;

    public ComponentContainer() {
        this(new HashMap<>());
    }

    public ComponentContainer(Map<Class<?>, Optional<Component>> components) {
        this.components = components;
    }

    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return (Optional<T>) components.getOrDefault(type, Optional.empty());
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.containsKey(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        if (value == null) {
            removeComponent(type);
        } else {
            components.put(type, Optional.of(value));
        }
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.remove(type);
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.keySet();
    }
}
