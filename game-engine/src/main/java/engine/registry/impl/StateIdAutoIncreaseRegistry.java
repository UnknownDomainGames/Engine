package engine.registry.impl;

import engine.registry.Registrable;
import engine.state.IncludeState;
import engine.state.State;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class StateIdAutoIncreaseRegistry<T extends Registrable<T>, S extends State<T, S>> extends StateIdRegistry<T, S> {

    private final AtomicInteger nextId = new AtomicInteger(0);

    public StateIdAutoIncreaseRegistry(Class<T> entryType, Class<S> stateType) {
        super(entryType, stateType);
        if (!IncludeState.class.isAssignableFrom(entryType)) {
            throw new IllegalArgumentException("Entry type " + entryType.getName() + " is not implemented IncludeState");
        }
    }

    public StateIdAutoIncreaseRegistry(Class<T> entryType, Class<S> stateType, String name) {
        super(entryType, stateType, name);
        if (!IncludeState.class.isAssignableFrom(entryType)) {
            throw new IllegalArgumentException("Entry type " + entryType.getName() + " is not implemented IncludeState");
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        super.register(obj);
        for (S state : ((IncludeState<T, S>) obj).getStateManager().getStates()) {
            setStateId(state, nextId.getAndIncrement());
        }
        return obj;
    }
}
