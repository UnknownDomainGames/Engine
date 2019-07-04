package nullengine.registry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Supplier;

public interface RegistryManager {

    /**
     * Register a registrable object to game
     *
     * @param obj The target we want to register
     */
    <T extends RegistryEntry<T>> T register(@Nonnull T obj);

    /**
     * Register a registrable object to game
     *
     * @param objs The target we want to register
     */
    default <T extends RegistryEntry<T>> void registerAll(@Nonnull T... objs) {
        for (T obj : objs) {
            register(obj);
        }
    }


    /**
     * @param type
     * @param supplier
     * @param <T>
     * @deprecated Provide for {@link nullengine.event.mod.ModRegistrationEvent.Construction}. Waiting to remove.
     */
    <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier);

    /**
     * @param type The type of the registry contained
     * @return The registry for this type
     */
    @Nonnull
    <T extends RegistryEntry<T>> Registry<T> getRegistry(Class<T> type);

    /**
     * @param type The type of the registry contained
     * @return If this registry exist
     */
    <T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type);

    @Nonnull
    Collection<Entry<Class<?>, Registry<?>>> getEntries();
}
