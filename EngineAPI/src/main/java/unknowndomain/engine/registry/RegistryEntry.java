package unknowndomain.engine.registry;

import com.google.common.reflect.TypeToken;

import unknowndomain.engine.client.resource.ResourcePath;

public interface RegistryEntry<T> {

    ResourcePath getRegistryName();

    Class<T> getRegistryType();

    T setRegistryName(ResourcePath location);

    default T setRegistryName(String name) {
        return setRegistryName(new ResourcePath(name));
    }

    abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {

        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };
        private ResourcePath location;

        @Override
        public final ResourcePath getRegistryName() {
            return location;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T setRegistryName(ResourcePath location) {
            this.location = location;
            return (T) this;
        }

        public final T setRegistryName(String domain, String path) {
            return setRegistryName(new ResourcePath(domain, path));
        }

        public final T setRegistryName(String path) {
            throw new UnsupportedOperationException();
            //TODO:
            //return setRegistryName(new ResourceLocation(domain, path));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final Class<T> getRegistryType() {
            return (Class<T>) token.getRawType();
        }

        ;
    }
}
