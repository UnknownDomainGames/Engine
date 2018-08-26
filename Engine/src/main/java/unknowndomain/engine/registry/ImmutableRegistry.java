package unknowndomain.engine.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ImmutableRegistry<T extends RegistryEntry<T>> implements Registry<T> {
    private final Class<T> registryType;
    private final String name;
    private final ImmutableMap<String, T> nameToObject;
    private final ImmutableMap<String, Integer> nameToId;
    private final ImmutableList<T> idToObject;

    private ImmutableRegistry(Class<T> registryType, String name, ImmutableMap<String, T> nameToObject,
                              ImmutableMap<String, Integer> nameToId, ImmutableList<T> idToObject) {
        this.registryType = registryType;
        this.name = name;
        this.nameToObject = nameToObject;
        this.nameToId = nameToId;
        this.idToObject = idToObject;
    }

    public static <T extends RegistryEntry<T>> ImmutableRegistry<T> freeze(Registry<T> registry) {
        return new ImmutableRegistry<>(registry.getRegistryEntryType(),
                registry.getRegistryName(), ImmutableMap.<String, T>builder().putAll(registry.getEntries()).build(),
                ImmutableMap.<String, Integer>builder().putAll(registry.getValues().stream().map(v ->
                        Pair.of(v.getUniqueName(),
                                registry.getId(v.getUniqueName())))
                        .collect(Collectors.toList())).build(),
                ImmutableList.sortedCopyOf(Comparator.comparingInt(registry::getId), registry.getValues())
        );
    }

    public static <T extends RegistryEntry<T>> ImmutableRegistry<T> synchronize(Registry<T> registry,
                                                                                Map<String, Integer> mapping) {
        return new ImmutableRegistry<>(registry.getRegistryEntryType(),
                registry.getRegistryName(), ImmutableMap.<String, T>builder().putAll(registry.getEntries()).build(),
                ImmutableMap.<String, Integer>builder().putAll(registry.getValues().stream().map(v -> Pair.of(v.getLocalName(), mapping.get(v.getLocalName()))).collect(Collectors.toList())).build(),
                ImmutableList.sortedCopyOf(Comparator.comparingInt(a -> mapping.get(a.getLocalName())), registry.getValues())
        );
    }

    @Override
    public Class<T> getRegistryEntryType() {
        return registryType;
    }

    @Override
    public String getRegistryName() {
        return name;
    }

    @Override
    public T register(T obj) throws RegisterException {
        throw new RegisterException();
    }

    @Override
    public T getValue(String registryName) {
        Validate.notNull(registryName);
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(T value) {
        Validate.notNull(value);
        return value.getUniqueName();
    }

    @Override
    public boolean containsKey(String key) {
        Validate.notNull(key);
        return nameToObject.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        Validate.notNull(value);
        return nameToObject.containsKey(value.getUniqueName());
    }

    @Override
    public Set<String> getKeys() {
        return nameToObject.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return idToObject;
    }

    @Override
    public int getId(T obj) {
        Validate.notNull(obj);
        return nameToId.get(obj.getUniqueName());
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getLocalName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return nameToObject.entrySet();
    }
}