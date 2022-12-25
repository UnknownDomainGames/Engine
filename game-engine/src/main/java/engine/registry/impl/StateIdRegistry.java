package engine.registry.impl;

import engine.registry.Registrable;
import engine.state.State;
import engine.state.StateIncludedRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.lang.reflect.Array;
import java.util.Arrays;

public class StateIdRegistry<T extends Registrable<T>, S extends State<T, S>> extends BaseRegistry<T> implements StateIncludedRegistry<T, S> {

    protected S[] idToState;
    protected Object2IntMap<S> stateToId = new Object2IntOpenHashMap<>();

    @SuppressWarnings("unchecked")
    public StateIdRegistry(Class<T> entryType, Class<S> stateType) {
        super(entryType);
        this.idToState = (S[]) Array.newInstance(stateType, 128);
    }

    @SuppressWarnings("unchecked")
    public StateIdRegistry(Class<T> entryType, Class<S> stateType, String name) {
        super(entryType, name);
        this.idToState = (S[]) Array.newInstance(stateType, 128);
    }

    @Override
    public int getStateId(S state) {
        return stateToId.get(state);
    }

    protected void ensureCapacity(int capacity) {
        int oldLength = idToState.length;
        if (oldLength < capacity) {
            int newLength = Math.max(oldLength << 1, capacity);
            idToState = Arrays.copyOf(idToState, newLength);
        }
    }

    @Override
    public void setStateId(S state, int id) {
        ensureCapacity(id + 1);
        stateToId.put(state, id);
        setIdUnsafe(state, id);
    }

    @Override
    public S getStateFromId(int id) {
        return idToState[id];
    }

    protected void setIdUnsafe(S entry, int id) {
        idToState[id] = entry;
    }
}
