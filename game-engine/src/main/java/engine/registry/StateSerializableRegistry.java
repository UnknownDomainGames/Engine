package engine.registry;

import engine.state.IncludeState;
import engine.state.State;
import engine.state.StateIncludedRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public interface StateSerializableRegistry<T extends Registrable<T>, S extends State<T, S>> extends StateIncludedRegistry<T, S> {

    /**
     * Write the state to an identifier.
     */
    String serializeState(S state);

    /**
     * Read the state from an identifier that generated by <code>writeState</code> method.
     */
    S deserializeState(String state);

    @SuppressWarnings("unchecked")
    default Map<String, Integer> collectToSerializedIdMap() {
        var map = new HashMap<String, Integer>();
        for (T value : getValues()) {
            for (S state : ((IncludeState<T, S>) value).getStateManager().getStates()) {
                map.put(serializeState(state), getStateId(state));
            }
        }
        return map;
    }

    default void applyFromSerializedIdMap(Map<String, Integer> map, BiConsumer<S, Integer> applier) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            applier.accept(deserializeState(entry.getKey()), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    default void synchronizeIdFromSerializedIdMap(Map<String, Integer> map) {
        applyFromSerializedIdMap(map, ((SynchronizableStateRegistry<T, S>) this)::syncState);
    }

}
