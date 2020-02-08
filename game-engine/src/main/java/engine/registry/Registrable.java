package engine.registry;

import com.google.common.reflect.TypeToken;

/**
 * The registry entry, which is the registrable object reference.
 *
 * @param <T> The real type of this object
 * @see Registry
 * @see Impl
 */
public interface Registrable<T> {

    Class<T> getEntryType();

    /**
     * Set the an local name for this object.
     *
     * @param name The local name, which is unique under YOUR mod and THIS registry type!
     * @return this
     */
    T name(String name);

    T name(Name name);

    /**
     * Get the name with its "namespaces" which is mod container and registry.
     */
    Name getName();

    int getId();

    abstract class Impl<T extends Registrable<T>> implements Registrable<T> {
        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };
        private Name name;

        private int id;

        @SuppressWarnings("unchecked")
        @Override
        public final Class<T> getEntryType() {
            return (Class<T>) token.getRawType();
        }

        @Override
        public final Name getName() {
            return name;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T name(String name) {
            if (this.name != null) throw new Error("Duplicated register " + name);
            this.name = Name.fromString(name);
            return (T) this;
        }

        @Override
        public final T name(Name name) {
            if (this.name != null) throw new Error("Duplicated register " + name);
            this.name = name;
            return (T) this;
        }

        @Override
        public final int getId() {
            return id;
        }
    }
}
