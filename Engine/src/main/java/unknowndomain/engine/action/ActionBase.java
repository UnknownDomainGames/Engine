package unknowndomain.engine.action;

import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.registry.Impl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

class ActionBase extends Impl<Action> implements Action {
    private final Consumer<GameContext> actionHandler;

    ActionBase(Consumer<GameContext> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public void onActionStart(GameContext context) {
        actionHandler.accept(context);
    }

    static class Keepable extends ActionBase implements Action.Keepable {
        private final BiConsumer<GameContext, Integer> keepHandler;
        private final BiConsumer<GameContext, Integer> endHandler;

        Keepable(Consumer<GameContext> actionHandler, BiConsumer<GameContext, Integer> keepHandler, BiConsumer<GameContext, Integer> endHandler) {
            super(actionHandler);
            this.keepHandler = keepHandler;
            this.endHandler = endHandler;
        }

        @Override
        public void onActionKeep(GameContext context, int tickElapsed) {
            if (keepHandler != null)
                keepHandler.accept(context, tickElapsed);
        }

        @Override
        public void onActionEnd(GameContext context, int tickElapsed) {
            if (endHandler != null)
                endHandler.accept(context, tickElapsed);
        }
    }
}
