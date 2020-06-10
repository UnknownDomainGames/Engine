package engine.registry;

import engine.annotation.Internal;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface RegistryManager {

    Collection<Registry<?>> getRegistries();

    /**
     * @param type
     * @param supplier
     * @param <T>
     * @deprecated Provide for {@link engine.event.mod.ModRegistrationEvent.Construction}. Waiting to remove.
     */
    @Deprecated
    @Internal
    <T extends Registrable<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier);

    /**
     * @param type The type of the registry contained
     * @return The registry for this type
     */
    <T extends Registrable<T>> Optional<Registry<T>> getRegistry(Class<T> type);

    /**
     * @param type The type of the registry contained
     * @return If this registry exist
     */
    <T extends Registrable<T>> boolean hasRegistry(Class<T> type);
}
