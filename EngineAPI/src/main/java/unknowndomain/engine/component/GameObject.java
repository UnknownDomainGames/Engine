package unknowndomain.engine.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 *
 */
public interface GameObject {

    @Nullable
    <T extends Component> T getComponent(@Nonnull Class<T> type);

    <T extends Component> boolean hasComponent(@Nonnull Class<T> type);

    <T extends Component> void setComponent(@Nonnull Class<T> type, T value);

}
