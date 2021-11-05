package engine.state;

import engine.registry.Registrable;
import engine.registry.Registry;

public interface StateIncludedRegistry<T extends Registrable<T>, S extends State<T, S>> extends Registry<T> {

    int getStateId(S state);

    void setStateId(S state, int id);

    S getStateFromId(int id);
}
