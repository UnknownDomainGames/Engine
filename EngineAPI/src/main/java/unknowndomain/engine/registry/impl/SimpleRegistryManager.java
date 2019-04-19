package unknowndomain.engine.registry.impl;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public class SimpleRegistryManager implements RegistryManager {

    private final Map<Class<?>, Registry<?>> registries;
    private final Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterTasks;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
        afterTasks = Maps.newHashMap();
    }

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries, Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterTasks) {
        this.registries = registries;
        this.afterTasks = afterTasks;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        requireNonNull(type);
        if (!hasRegistry(type)) {
            throw new UnsupportedOperationException(String.format("Has not registered registry for type %s", type.getSimpleName()));
        }
        return (Registry<T>) registries.get(type);
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(@NonNull Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public <T extends RegistryEntry<T>> T register(@NonNull T obj) {
        T t = getRegistry(obj.getEntryType()).register(obj);
        for (Entry<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> entry : afterTasks.entrySet()) {
            if (entry.getKey().isInstance(t)) {
                for (Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>> pair : entry.getValue()) {
                    pair.getRight().accept(t, getRegistry(pair.getLeft()));
                }
            }
        }
        return t;
    }

    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
