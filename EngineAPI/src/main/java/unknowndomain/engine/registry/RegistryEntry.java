package unknowndomain.engine.registry;

public interface RegistryEntry<T> {

    String getRegisteredName();

    Class<T> getRegistryType();

    T setRegistryName(String location);

}
