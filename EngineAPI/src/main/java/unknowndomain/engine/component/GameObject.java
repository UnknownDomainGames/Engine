package unknowndomain.engine.component;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 *
 */
public interface GameObject {

    @Nonnull
    <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type);

    <T extends Component> boolean hasComponent(@Nonnull Class<T> type);

    <T extends Component> void setComponent(@Nonnull Class<T> type, @Nonnull T value);

    <T extends Component> void removeComponent(@Nonnull Class<T> type);

}
