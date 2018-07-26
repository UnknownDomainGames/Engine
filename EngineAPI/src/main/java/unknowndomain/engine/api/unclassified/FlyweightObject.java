package unknowndomain.engine.api.unclassified;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The flyweight object in game. Once it's loaded, it will not be unload unless the "owner" of this is unloaded
 *
 * @param <E> The runtime entity it could produce
 * @param <C> The runtime entity the creation entity depend on
 */
public interface FlyweightObject<E extends RuntimeEntity<C>, C extends RuntimeEntity<?>> {
    /**
     * e.g. in minecraft block corresponds to tileentity and the item corresponds to itemstack
     *
     * @param context The context of it depends
     * @return The craeted entity
     */
    E createEntity(C context);

    @Nullable
    <T> T getBehavior(@Nonnull Class<T> type);

    boolean putBehavior(@Nonnull Object module);

    <T> void unloadBehavior(@Nonnull Class<T> type);
}
