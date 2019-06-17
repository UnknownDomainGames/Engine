package unknowndomain.engine.event.game;

import unknowndomain.engine.game.Game;

public class GameTerminationEvent extends GameEvent {

    GameTerminationEvent(Game game) {
        super(game);
    }

    public static class Marked extends GameTerminationEvent {

        public Marked(Game game) {
            super(game);
        }
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
