package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.Game;

public class GameEvent implements Event {

    private final Game game;

    public GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public static final class Ready extends GameEvent {
        public Ready(Game game) {
            super(game);
        }
    }
}
