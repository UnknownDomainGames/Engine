package unknowndomain.engine.api.unclassified;

/**
 * The flyweight object in game. Once it's loaded, it will not be unload unless the "owner" of this is unloaded
 *
 * @param <E> The runtime entity it could produce
 * @param <C> The runtime entity the creation entity depend on
 */
public interface Prototype<E extends RuntimeObject, C extends RuntimeObject> {
    /**
     * e.g. in minecraft block corresponds to tileentity and the item corresponds to itemstack
     *
     * @param context The context of it depends
     * @return The created entity
     */
    E createObject(C context);
}
