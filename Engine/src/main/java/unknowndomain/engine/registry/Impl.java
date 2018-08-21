package unknowndomain.engine.registry;

import com.google.common.reflect.TypeToken;

public abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {

    private final TypeToken<T> token = new TypeToken<T>(getClass()) {
    };
    private String registeredName;

    @Override
    public final String getRegisteredName() {
        return registeredName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T setRegistryName(String location) {
        if (this.registeredName != null) throw new Error("Duplicated register");
        this.registeredName = location;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Class<T> getRegistryType() {
        return (Class<T>) token.getRawType();
    }

    @Override
    public String toString() {
        return token + "{" +
                "path='" + registeredName + '\'' +
                '}';
    }
}
