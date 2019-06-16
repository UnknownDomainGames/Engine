package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.Game;

public class GameStartEvent implements Event {

    private final Game game;

    private GameStartEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public static class Pre extends GameStartEvent {

        public Pre(Game game) {
            super(game);
        }
    }

    public static class Post extends GameStartEvent {

        public Post(Game game) {
            super(game);
        }
    }
}
