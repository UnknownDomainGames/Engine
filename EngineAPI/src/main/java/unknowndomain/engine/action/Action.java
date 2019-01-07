package unknowndomain.engine.action;

import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.registry.RegistryEntry;

/**
 * 
 * @Deprecated Merged with KeyBinding
 */
@Deprecated
public interface Action extends RegistryEntry<Action> {
    void onActionStart(GameContext context);

    interface Keepable extends Action {
        void onActionKeep(GameContext context, int tickElapsed);

        void onActionEnd(GameContext context, int tickElapsed);
    }
}
