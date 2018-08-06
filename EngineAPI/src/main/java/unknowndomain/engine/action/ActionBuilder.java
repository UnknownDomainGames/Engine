package unknowndomain.engine.action;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.function.Consumer;

public class ActionBuilder {
    private ResourcePath path;
    private Consumer<GameContext> actionHandler;
    private Consumer<GameContext> startHandler;
    private Consumer<GameContext> endHandler;

    public ActionBuilder(ResourcePath path) {
        this.path = path;
    }

    public ActionBuilder setActionHandler(Consumer<GameContext> actionHandler) {
        this.actionHandler = actionHandler;
        return this;
    }

    public ActionBuilder setStartHandler(Consumer<GameContext> startHandler) {
        this.startHandler = startHandler;
        return this;
    }

    public ActionBuilder setEndHandler(Consumer<GameContext> endHandler) {
        this.endHandler = endHandler;
        return this;
    }

    public Action build() {
        if (startHandler != null && endHandler != null) {
            new ActionBase.Keepable(actionHandler, startHandler, endHandler).setRegistryName(path);
        }
        return new ActionBase(actionHandler).setRegistryName(path);
    }
}