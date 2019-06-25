package nullengine.registry;

import com.google.common.reflect.TypeToken;

/**
 * The registry entry, which is the registrable object reference.
 *
 * @param <T> The real type of this object
 * @see Registry
 * @see Impl
 */
public interface RegistryEntry<T> {

    Class<T> getEntryType();

    /**
     * Set the an local name for this object.
     *
     * @param name The local name, which is unique under YOUR mod and THIS registry type!
     * @return this
     */
    T registerName(String name);

    /**
     * The user set local name for this object.
     *
     * @return The user set local name for this object.
     * @see #registerName(String)
     */
    String getRegisterName();

    int getId();

    /**
     * Get the long name with its "namespaces" which is mod container and registry.
     */
    String getUniqueName();

    abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {
        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };
        private String registeredName;

        private String uniqueName;
        private int id;

        @SuppressWarnings("unchecked")
        @Override
        public final Class<T> getEntryType() {
            return (Class<T>) token.getRawType();
        }

        @Override
        public final String getRegisterName() {
            return registeredName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T registerName(String name) {
            if (this.registeredName != null) throw new Error("Duplicated register " + name);
            this.registeredName = name;
            return (T) this;
        }

        @Override
        public final String getUniqueName() {
            return uniqueName;
        }

        @Override
        public final int getId() {
            return id;
        }

        @Override
        public String toString() {
            return token.getRawType().getSimpleName() + "{" +
                    "uniqueName='" + uniqueName + '\'' +
                    '}';
        }
    }
}
