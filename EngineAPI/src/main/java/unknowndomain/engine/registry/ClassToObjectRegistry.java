package unknowndomain.engine.registry;

public interface ClassToObjectRegistry<T extends RegistryEntry<T>> extends Registry<T> {

    T get(Class<T> clazz);

}
