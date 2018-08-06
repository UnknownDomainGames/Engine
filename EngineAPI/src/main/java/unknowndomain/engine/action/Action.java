package unknowndomain.engine.action;

import com.google.common.base.Preconditions;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.registry.RegistryEntry;

public interface Action extends RegistryEntry<Action> {
    static ActionBuilder builder(ResourcePath path) {
        Preconditions.checkNotNull(path);
        return Internal0.INTERAL.apply(path);
    }

    static ActionBuilder builder(String id) {
        Preconditions.checkNotNull(id);
        return Internal0.INTERAL.apply(new ResourcePath(id));
    }

    void onAction(GameContext context);

    interface Keepable extends Action {
        void onActionStart(GameContext context);

        void onActionEnd(GameContext context);
    }
}
