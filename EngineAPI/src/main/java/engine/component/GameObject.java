package engine.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Represent the runtime data for a "thing". The life-cycle of it will depend on its context.
 */
public interface GameObject<T extends GameObject> {

    <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type);

    <C extends Component> boolean hasComponent(@Nonnull Class<C> type);

    <C extends Component> T setComponent(@Nonnull Class<C> type, @Nullable C value);

    <C extends Component> T removeComponent(@Nonnull Class<C> type);

    @Nonnull
    Set<Class<?>> getComponents();
}
