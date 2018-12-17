package unknowndomain.engine.registry;

import unknowndomain.engine.mod.ModContainer;

/**
 * The registry entry, which is the registrable object reference.
 *
 * @param <T> The real type of this object
 * @see Registry
 */
public interface RegistryEntry<T> {
    Class<T> getRegistryType();

    /**
     * Set the an local name for this object.
     *
     * @param name The local name, which is unique under YOUR mod and THIS registry type!
     * @return this
     */
    T localName(String name);

    /**
     * The user set local name for this object.
     *
     * @return The user set local name for this object.
     * @see #localName(String)
     */
    String getLocalName();

    /**
     * Return the mod which provides and registers this object.
     * <p>This getter will only valid after the registry stage of the game which triggered by {@link unknowndomain.engine.event.registry.RegisterEvent}</p>
     *
     * @return The mod mod container
     * @see unknowndomain.engine.mod.ModContainer
     * @see unknowndomain.engine.mod.ModIdentifier
     * @see unknowndomain.engine.event.registry.RegisterEvent
     */
    ModContainer getOwner();

    /**
     * Get the registry managing this object.
     * <p>This getter will only valid after the registry stage of the game which triggered by {@link unknowndomain.engine.event.registry.RegisterEvent}</p>
     *
     * @return The registry which managing this object.
     * @see unknowndomain.engine.event.registry.RegisterEvent
     */
    Registry<? extends RegistryEntry<T>> getAssignedRegistry();

    /**
     * Get the long name with its "namespaces" which is mod container and registry.
     *
     * @see #getAssignedRegistry()
     * @see #getOwner()
     */
    default String getUniqueName() {
        return getOwner().getModId().concat(".")
                .concat(getAssignedRegistry().getRegistryName()).concat(".")
                .concat(getLocalName());
    }
    int getID();
}
