package unknowndomain.engine.action;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.registry.RegistryEntry;

import java.util.function.Consumer;

class ActionBase extends RegistryEntry.Impl<Action> implements Action {
    private final Consumer<GameContext> actionHandler;

    ActionBase(Consumer<GameContext> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public void onAction(GameContext context) {
        actionHandler.accept(context);
    }

    static class Keepable extends ActionBase implements Action.Keepable {
        private final Consumer<GameContext> startHandler;
        private final Consumer<GameContext> endHandler;

        Keepable(Consumer<GameContext> actionHandler, Consumer<GameContext> startHandler, Consumer<GameContext> endHandler) {
            super(actionHandler);
            this.startHandler = startHandler;
            this.endHandler = endHandler;
        }

        @Override
        public void onActionStart(GameContext context) {
            startHandler.accept(context);
        }

        @Override
        public void onActionEnd(GameContext context) {
            endHandler.accept(context);
        }
    }
}
