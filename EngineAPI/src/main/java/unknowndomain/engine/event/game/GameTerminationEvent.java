package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.Game;

public class GameTerminationEvent implements Event {

    private final Game game;

    private GameTerminationEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public static class Pre extends GameTerminationEvent {

        public Pre(Game game) {
            super(game);
        }
    }

    public static class Post extends GameTerminationEvent {

        public Post(Game game) {
            super(game);
        }
    }
}
