package unknowndomain.engine.client.resource;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;

import java.io.InputStream;

public interface ResourceLoader<T extends Resource> {

    Class<T> getResourceType();

    T load(InputStream inputStream);

    abstract class Impl<T extends Resource> implements ResourceLoader<T> {

        private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        };

        @SuppressWarnings("unchecked")
        @Override
        public final Class<T> getResourceType() {
            return (Class<T>) token.getRawType();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("token", token).toString();
        }
    }

}
