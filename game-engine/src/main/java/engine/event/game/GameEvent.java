package engine.event.game;

import engine.event.Event;
import engine.game.Game;

public class GameEvent implements Event {

    private final Game game;

    GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
