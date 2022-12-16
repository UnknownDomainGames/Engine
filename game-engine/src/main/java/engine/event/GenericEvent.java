package engine.event;

import java.lang.reflect.Type;

/**
 * Every generic event class should implement this interface.
 */
public interface GenericEvent<T> extends Event {
    Type getGenericType();

    abstract class Impl<T> implements GenericEvent<T> {

        private final Type genericType;

        public Impl(Type genericType) {
            this.genericType = genericType;
        }

        @Override
        public Type getGenericType() {
            return genericType;
        }
    }
}
