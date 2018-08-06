package unknowndomain.engine.action;

import unknowndomain.engine.GameContext;

import java.util.function.Consumer;

public interface ActionBuilder {
    ActionBuilder setActionHandler(Consumer<GameContext> actionHandler);

    ActionBuilder setStartHandler(Consumer<GameContext> startHandler);

    ActionBuilder setEndHandler(Consumer<GameContext> endHandler);

    Action build();
}
