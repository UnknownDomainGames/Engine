package nullengine.registry.impl;

import nullengine.registry.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;

public class SimpleRegistryManager implements RegistryManager {

    private final Map<Class<?>, Registry<?>> registries;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
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
    public <T extends Registrable<T>> T register(@NonNull T obj) {
        return getRegistry(obj.getEntryType())
                .orElseThrow(() -> new RegistrationException(format("Not found registry \"%s\"", obj.getEntryType().getSimpleName())))
                .register(obj);
    }

    @Override
    public <T extends Registrable<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier) {
        if (registries.containsKey(type)) {
            throw new RegistryException("Registry has been registered");
        }
        registries.put(type, supplier.get());
    }

    @Nonnull
    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
