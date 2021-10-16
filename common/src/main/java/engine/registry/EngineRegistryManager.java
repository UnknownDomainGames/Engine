package engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class EngineRegistryManager implements RegistryManager {

    private final Map<Class<?>, Registry<?>> registries;

    public EngineRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    @Override
    public <T extends Registrable<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier) {
        if (registries.containsKey(type)) {
            throw new IllegalStateException("Registry has been registered");
        }
        registries.put(type, supplier.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Registrable<T>> Optional<Registry<T>> getRegistry(Class<T> type) {
        return Optional.ofNullable((Registry<T>) registries.get(type));
    }

    @Override
    public <T extends Registrable<T>> boolean hasRegistry(Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public Collection<Registry<?>> getRegistries() {
        return registries.values();
    }
}
