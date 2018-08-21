package unknowndomain.engine.action;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.registry.RegistryEntry;

public interface Action extends RegistryEntry<Action> {
    void onActionStart(GameContext context);

    interface Keepable extends Action {
        void onActionKeep(GameContext context, int tickElapsed);

        void onActionEnd(GameContext context, int tickElapsed);
    }
}
