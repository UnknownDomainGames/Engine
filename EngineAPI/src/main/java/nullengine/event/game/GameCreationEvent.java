package nullengine.event.game;

import nullengine.game.Game;

public class GameCreationEvent extends GameEvent {
    GameCreationEvent(Game game) {
        super(game);
    }

    public static class Pre extends GameCreationEvent{
        public Pre(Game game) {
            super(game);
        }
    }

    public static class Post extends GameCreationEvent{
        public Post(Game game) {
            super(game);
        }
    }
}
