package unknowndomain.engine.event.registry;

import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.event.Event;

public class GameReadyEvent implements Event {
    private GameContext context;

    public GameReadyEvent(GameContext context) {
        this.context = context;
    }

    public GameContext getContext() {
        return context;
    }
}
