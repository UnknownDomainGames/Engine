package unknowndomain.engine.component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentContainer implements GameObject {

    private final Map<Class<?>, Optional<Component>> components;

    public ComponentContainer() {
        this(new HashMap<>());
    }

    public ComponentContainer(Map<Class<?>, Optional<Component>> components) {
        this.components = components;
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return (Optional<T>) components.getOrDefault(type, Optional.empty());
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.containsKey(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, T value) {
        components.put(type, Optional.of(value));
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.remove(type);
    }
}
