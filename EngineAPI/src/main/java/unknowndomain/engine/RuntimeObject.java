package unknowndomain.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 *
 */
public interface RuntimeObject {
    @Nullable
    <T> T getComponent(@Nonnull String name);

    @Nullable
    <T> T getComponent(@Nonnull Class<T> type);

    @Nullable
    <T> T getBehavior(Class<T> type);

    interface Componnet {

    }

    interface Behavior {

    }
}
