package unknowndomain.engine.action;

import unknowndomain.engine.game.GameContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 
 * @Deprecated Merged with KeyBinding
 */
@Deprecated
public interface ActionBuilder {
    ActionBuilder setStartHandler(Consumer<GameContext> startHandler);

    ActionBuilder setKeepHandler(BiConsumer<GameContext, Integer> actionHandler);

    ActionBuilder setEndHandler(BiConsumer<GameContext, Integer> endHandler);

    Action build();
}
