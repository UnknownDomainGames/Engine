package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.Game;

public class GameEvent implements Event {

    private final Game game;

    GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
