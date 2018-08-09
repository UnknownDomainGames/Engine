package unknowndomain.engine.action;

import unknowndomain.engine.registry.Registry;

public interface ActionManager extends Registry<Action> {
    void start(String action);

    void end(String action);
}
