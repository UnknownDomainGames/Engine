package unknowndomain.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 *
 */
public interface RuntimeObject {

    @Nullable
    <T extends Component> T getComponent(@Nonnull Class<T> type);

    <T extends Component> boolean hasComponent(@Nonnull Class<T> type);

    interface Component {

    }
}
