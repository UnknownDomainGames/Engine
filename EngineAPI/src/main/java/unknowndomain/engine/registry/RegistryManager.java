package unknowndomain.engine.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map.Entry;

public interface RegistryManager {

    /**
     * @param type The type of the registry contained
     * @return The registry for this type
     */
    @Nullable
    <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type);

    /**
     * @param type The type of the registry contained
     * @return If this registry exist
     */
    <T extends RegistryEntry<T>> boolean hasRegistry(@Nonnull Class<T> type);

    Collection<Entry<Class<?>, Registry<?>>> getEntries();

    /**
     * Register a registerable object to game
     *
     * @param obj The target we want to register
     */
    <T extends RegistryEntry<T>> void register(@Nonnull T obj);
}
