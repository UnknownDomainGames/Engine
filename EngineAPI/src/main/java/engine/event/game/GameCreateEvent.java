package engine.event.game;

import engine.game.Game;

public class GameCreateEvent extends GameEvent {
    GameCreateEvent(Game game) {
        super(game);
    }

    public static class Pre extends GameCreateEvent {
        public Pre(Game game) {
            super(game);
        }
    }

    public static class Post extends GameCreateEvent {
        public Post(Game game) {
            super(game);
        }
    }
}
