package nullengine.event.game;

import nullengine.event.Event;
import nullengine.game.Game;

public class GameEvent implements Event {

    private final Game game;

    GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
