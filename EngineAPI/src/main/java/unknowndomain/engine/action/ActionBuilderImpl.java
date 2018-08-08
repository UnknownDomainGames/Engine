package unknowndomain.engine.action;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

class ActionBuilderImpl implements ActionBuilder {
    private ResourcePath path;
    private Consumer<GameContext> startHandler;
    private BiConsumer<GameContext, Integer> keepHandler;
    private BiConsumer<GameContext, Integer> endHandler;

    ActionBuilderImpl(ResourcePath path) {
        this.path = path;
    }

    @Override
    public ActionBuilder setStartHandler(Consumer<GameContext> startHandler) {
        Validate.notNull(startHandler);
        this.startHandler = startHandler;
        return this;
    }

    @Override
    public ActionBuilder setKeepHandler(BiConsumer<GameContext, Integer> actionHandler) {
        Validate.notNull(actionHandler);
        this.keepHandler = actionHandler;
        return this;
    }

    @Override
    public ActionBuilder setEndHandler(BiConsumer<GameContext, Integer> endHandler) {
        Validate.notNull(endHandler);
        this.endHandler = endHandler;
        return this;
    }

    @Override
    public Action build() {
        if (startHandler == null) throw new Error("Start action cannot be null!");
        if (keepHandler != null || endHandler != null) {
            return new ActionBase.Keepable(startHandler, keepHandler, endHandler).setRegistryName(path);
        }
        return new ActionBase(startHandler).setRegistryName(path);
    }
}