package unknowndomain.engine.action;

import com.google.common.base.Preconditions;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.registry.RegistryEntry;

public interface Action extends RegistryEntry<Action> {
    static ActionBuilder builder(String path) {
        Preconditions.checkNotNull(path);
        return new ActionBuilderImpl(path);
    }

    void onActionStart(GameContext context);

    interface Keepable extends Action {
        void onActionKeep(GameContext context, int tickElapsed);

        void onActionEnd(GameContext context, int tickElapsed);
    }
}
