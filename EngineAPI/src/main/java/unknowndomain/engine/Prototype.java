package unknowndomain.engine;

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
     * @param gameContext The global runtime context
     * @param context        The context of it depends
     * @return The created entity
     */
    E createObject(GameContext gameContext, C context);

//    List<Action<E, C>> getActions();

//    interface Action<E extends RuntimeObject, C extends RuntimeObject> extends RegistryEntry<Action<E, C>> {
//        void start(C context, E object);
//    }
}
