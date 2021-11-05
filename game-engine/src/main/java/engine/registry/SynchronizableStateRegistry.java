package engine.registry;

import engine.state.State;

import java.util.Map;

public interface SynchronizableStateRegistry<T extends Registrable<T>, S extends State<T, S>> extends Registry<T> {

    /**
     * Get the id before synchronization
     *
     * @return
     */
    int getIntrinsicStateId(S state);

    void syncState(S state, int id);

    default void syncStates(Map<S, Integer> map) {
        if (getEntries().size() != map.size())
            throw new IllegalArgumentException("Sync map size does not match with size of registered objects");
        map.forEach(this::syncState);
    }

    void unsyncStates();

}
