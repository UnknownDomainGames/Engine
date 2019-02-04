package unknowndomain.engine.event.game;

import unknowndomain.engine.game.Game;

public class GameTerminationEvent extends GameEvent {

    public GameTerminationEvent(Game game) {
        super(game);
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
