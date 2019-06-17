package unknowndomain.engine.event.game;

import unknowndomain.engine.game.Game;

public class GameStartEvent extends GameEvent {

    GameStartEvent(Game game) {
        super(game);
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
