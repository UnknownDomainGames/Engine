package engine.state;

public interface IncludeState<T, S extends State<T, S>> {
    StateManager<T, S> getStateManager();
}
