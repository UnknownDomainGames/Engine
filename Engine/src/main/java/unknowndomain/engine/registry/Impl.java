package unknowndomain.engine.registry;

import com.google.common.reflect.TypeToken;

import unknowndomain.engine.mod.ModContainer;

public abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {
    private final TypeToken<T> token = new TypeToken<T>(getClass()) {
    };
    private String registeredName;

    private ModContainer modContainer;
    private Registry<T> registry;

    @SuppressWarnings("unchecked")
    @Override
    public final Class<T> getRegistryType() {
        return (Class<T>) token.getRawType();
    }

    @Override
    public String toString() {
        return token + "{" + "path='" + registeredName + '\'' + '}';
    }

    @Override
    public final String getLocalName() {
        return registeredName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T localName(String name) {
        if (this.registeredName != null)
            throw new Error("Duplicated register " + name);
        this.registeredName = name;
        return (T) this;
    }

    @Override
    public void setup(ModContainer modContainer, Registry<T> registry) {
        if (this.modContainer == null)
            this.modContainer = modContainer;
        if (this.registry == null)
            this.registry = registry;
    }

    @Override
    public ModContainer getOwner() {
        if (modContainer == null)
            throw new IllegalStateException("This method can only be called after the register stage of the game!");
        return modContainer;
    }

    @Override
    public Registry<T> getAssignedRegistry() {
        if (registry == null)
            throw new IllegalStateException("This method can only be called after the register stage of the game!");
        return registry;
    }

    @Override
    public int getID() {
        return getAssignedRegistry().getId((T) this);
    }

}
