package unknowndomain.engine.registry;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T> {
    int getId(T obj);

    int getId(String key);

    String getKey(int id);

    T getValue(int id);
}
