package unknowndomain.engine.action;

import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.registry.Registry;

public interface ActionManager extends Registry<Action> {
    void start(ResourcePath action);

    void end(ResourcePath action);
}
