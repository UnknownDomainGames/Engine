package nullengine.event.mod;

import nullengine.event.Event;
import nullengine.event.GenericEvent;
import nullengine.registry.Registry;
import nullengine.registry.RegistryEntry;
import nullengine.registry.RegistryManager;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public abstract class ModRegistrationEvent implements Event {
    private final RegistryManager manager;

    private ModRegistrationEvent(RegistryManager registryManager) {
        manager = registryManager;
    }

    public RegistryManager getRegistryManager() {
        return manager;
    }

    public static class Construction implements Event {

        private final RegistryManager manager;

        public Construction(RegistryManager registryManager) {
            manager = registryManager;
        }

        public <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier) {
            manager.addRegistry(type, supplier);
        }
    }

    public static class Pre extends ModRegistrationEvent {

        public Pre(RegistryManager registryManager) {
            super(registryManager);
        }
    }

    public static class Post extends ModRegistrationEvent {

        public Post(RegistryManager registryManager) {
            super(registryManager);
        }
    }

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
