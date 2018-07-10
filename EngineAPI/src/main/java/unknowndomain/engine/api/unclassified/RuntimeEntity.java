package unknowndomain.engine.api.unclassified;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 *
 * @param <Context> The type of it's dependent context
 */
public interface RuntimeEntity<Context extends RuntimeEntity<?>> {
    FlyweightObject<RuntimeEntity<Context>, Context> getDefinition();

    @Nullable
    <T> T getComponent(@Nonnull String name);

    @Nullable
    <T> T getComponent(@Nonnull Class<T> type);

    void onCreate(@Nonnull Context context);

    void onDestroy(@Nonnull Context context);
}
