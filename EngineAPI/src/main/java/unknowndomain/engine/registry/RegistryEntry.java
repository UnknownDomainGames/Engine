package unknowndomain.engine.registry;

import com.google.common.reflect.TypeToken;

public interface RegistryEntry<T> {

    String getRegistryName();

    Class<T> getRegistryType();

    T setRegistryName(String location);

    abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {

        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };
        private String location;

        @Override
        public final String getRegistryName() {
            return location;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T setRegistryName(String location) {
            if (this.location != null) throw new Error("Duplicated register");
            this.location = location;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final Class<T> getRegistryType() {
            return (Class<T>) token.getRawType();
        }
    }
}
