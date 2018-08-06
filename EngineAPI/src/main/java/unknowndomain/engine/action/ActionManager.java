package unknowndomain.engine.action;

import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.registry.Registry;

public interface ActionManager extends Registry<Action> {
    void perform(Player player, ResourcePath action);
}
