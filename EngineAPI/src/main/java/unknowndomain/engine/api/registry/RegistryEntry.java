package unknowndomain.engine.api.registry;

import com.google.common.reflect.TypeToken;

import unknowndomain.engine.api.util.DomainedPath;

public interface RegistryEntry<T> {

    DomainedPath getRegistryName();

    Class<T> getRegistryType();

    T setRegistryName(DomainedPath location);

    default T setRegistryName(String name) {
        return setRegistryName(new DomainedPath(name));
    }

    abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {

        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };
        private DomainedPath location;

        @Override
        public final DomainedPath getRegistryName() {
            return location;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T setRegistryName(DomainedPath location) {
            this.location = location;
            return (T) this;
        }

        public final T setRegistryName(String domain, String path) {
            return setRegistryName(new DomainedPath(domain, path));
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
