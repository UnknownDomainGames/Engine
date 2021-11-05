package engine.registry.impl;

import engine.registry.Registrable;
import engine.registry.SynchronizableStateRegistry;
import engine.state.State;

import java.util.HashMap;
import java.util.Map;

public abstract class SynchronizableStateIdRegistry<T extends Registrable<T>, S extends State<T, S>> extends StateIdAutoIncreaseRegistry<T, S> implements SynchronizableStateRegistry<T, S> {

    public SynchronizableStateIdRegistry(Class<T> entryType, Class<S> stateType) {
        super(entryType, stateType);
    }

    public SynchronizableStateIdRegistry(Class<T> entryType, Class<S> stateType, String name) {
        super(entryType, stateType, name);
    }

    private final Map<S, Integer> syncedMapping = new HashMap<>();

    @Override
    public int getIntrinsicStateId(S state) {
        return super.getStateId(state);
    }

    @Override
    public int getStateId(S state) {
        if (!syncedMapping.isEmpty()) {
            return syncedMapping.get(state);
        }
        return getIntrinsicStateId(state);
    }

    @Override
    public void syncState(S state, int id) {
        syncedMapping.put(state, id);
    }

    @Override
    public void unsyncStates() {
        syncedMapping.clear();
    }

}
