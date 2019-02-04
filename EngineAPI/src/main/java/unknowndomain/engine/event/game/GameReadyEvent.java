package unknowndomain.engine.event.game;

import unknowndomain.engine.game.Game;
import unknowndomain.engine.game.GameContext;

public class GameReadyEvent extends GameEvent {
    private GameContext context;

    public GameReadyEvent(Game game, GameContext context) {
        super(game);
        this.context = context;
    }

    public GameContext getContext() {
        return context;
    }
}
