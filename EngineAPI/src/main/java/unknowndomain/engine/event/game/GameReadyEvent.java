package unknowndomain.engine.event.game;

import unknowndomain.engine.game.Game;

public class GameReadyEvent extends GameEvent {

    public GameReadyEvent(Game game) {
        super(game);
    }
}
