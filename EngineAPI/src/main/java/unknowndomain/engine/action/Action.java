package unknowndomain.engine.action;

import com.google.common.base.Preconditions;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.registry.RegistryEntry;

public interface Action extends RegistryEntry<Action> {
    static ActionBuilder builder(ResourcePath path) {
        Preconditions.checkNotNull(path);
        return new ActionBuilderImpl(path);
    }

    static ActionBuilder builder(String id) {
        Preconditions.checkNotNull(id);
        return new ActionBuilderImpl(new ResourcePath(id));
    }

    void onActionStart(GameContext context);

    interface Keepable extends Action {
        void onActionKeep(GameContext context, int tickElapsed);

        void onActionEnd(GameContext context, int tickElapsed);
    }
}
