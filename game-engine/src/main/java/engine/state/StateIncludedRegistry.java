package engine.state;

public interface StateIncludedRegistry<S extends State> {

    /**
     * (Re)construct a id-to-state mapping for the registry
     * should call this just after register process is done
     * Client side should additionally call this after registry synchronization
     */
    void reconstructStateId();

    int getStateId(S state);

    S getStateFromId(int id);
}
