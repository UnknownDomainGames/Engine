package nullengine.event.mod;

import nullengine.Engine;
import nullengine.event.Event;
import nullengine.event.GenericEvent;
import nullengine.mod.ModContainer;
import nullengine.registry.Registry;
import nullengine.registry.RegistryEntry;
import nullengine.registry.RegistryManager;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public abstract class ModRegistrationEvent implements Event {

    private final ModContainer mod;
    private final RegistryManager manager;

    private ModRegistrationEvent(ModContainer mod, RegistryManager registryManager) {
        this.mod = mod;
        manager = registryManager;
    }

    public ModContainer getMod() {
        return mod;
    }

    public RegistryManager getRegistryManager() {
        return manager;
    }

    /**
     * @see ModContainer#getEventBus()
     */
    public static class Construction implements Event {

        private final ModContainer mod;
        private final RegistryManager manager;

        public Construction(ModContainer mod, RegistryManager registryManager) {
            this.mod = mod;
            manager = registryManager;
        }

        public ModContainer getMod() {
            return mod;
        }

        public <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier) {
            manager.addRegistry(type, supplier);
        }
    }

    /**
     * @see Engine#getEventBus()
     * @see ModContainer#getEventBus()
     */
    public static class Pre extends ModRegistrationEvent {

        public Pre(ModContainer mod, RegistryManager registryManager) {
            super(mod, registryManager);
        }
    }

    /**
     * @see Engine#getEventBus()
     * @see ModContainer#getEventBus()
     */
    public static class Post extends ModRegistrationEvent {

        public Post(ModContainer mod, RegistryManager registryManager) {
            super(mod, registryManager);
        }
    }

    /**
     * @see ModContainer#getEventBus()
     */
    public static class Register<T extends RegistryEntry<T>> implements GenericEvent<T> {

        private final Registry<T> registry;

        public Register(Registry<T> registry) {
            this.registry = registry;
        }

        @Override
        public Type getGenericType() {
            return registry.getEntryType();
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        public T register(@Nonnull T obj) {
            return registry.register(obj);
        }

        public void registerAll(@Nonnull T... objs) {
            registry.registerAll(objs);
        }
    }
}
