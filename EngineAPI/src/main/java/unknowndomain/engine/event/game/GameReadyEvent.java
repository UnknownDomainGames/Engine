package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.GameContext;

public class GameReadyEvent implements Event {
    private GameContext context;

    public GameReadyEvent(GameContext context) {
        this.context = context;
    }

    public GameContext getContext() {
        return context;
    }
}
