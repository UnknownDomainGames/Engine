package unknowndomain.engine.event.mod;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class RegistryConstructionEvent implements Event {
    private final Map<Class<?>, Registry<?>> registries;
    private final Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterRegistries;

    public RegistryConstructionEvent(Map<Class<?>, Registry<?>> registries, Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterRegistries) {
        this.registries = registries;
        this.afterRegistries = afterRegistries;
    }

    public void register(Registry<?> registry) {
        if (registries.containsKey(registry.getEntryType())) {
            throw new IllegalStateException(String.format("Registry<%s> has been registered.", registry.getEntryType().getSimpleName()));
        }
        registries.put(registry.getEntryType(), registry);
    }

    public <T extends RegistryEntry<T>, U extends RegistryEntry<U>> void registerPostTask(Class<T> depended, Class<U> depending, BiConsumer<T,Registry<U>> action){
        if (!registries.containsKey(depended)) {
            throw new IllegalStateException(String.format("Registry<%s> has not been registered before adding post tasks.", depended.getSimpleName()));
        }
        if (!registries.containsKey(depending)) {
            throw new IllegalStateException(String.format("Registry<%s> has not been registered before adding post tasks.", depending.getSimpleName()));
        }
        if(!afterRegistries.containsKey(depended)){
            afterRegistries.put(depended,Lists.newArrayList());
        }
        afterRegistries.get(depended).add(new ImmutablePair(depending, action));
    }
}
