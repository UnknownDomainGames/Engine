package unknowndomain.engine.action;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.function.Consumer;

public class ActionBuilderImpl implements ActionBuilder {
    static {
        Internal0.INTERAL = ActionBuilderImpl::new;
    }

    private ResourcePath path;
    private Consumer<GameContext> actionHandler;
    private Consumer<GameContext> startHandler;
    private Consumer<GameContext> endHandler;

    private ActionBuilderImpl(ResourcePath path) {
        this.path = path;
    }

    @Override
    public ActionBuilder setActionHandler(Consumer<GameContext> actionHandler) {
        this.actionHandler = actionHandler;
        return this;
    }

    @Override
    public ActionBuilder setStartHandler(Consumer<GameContext> startHandler) {
        this.startHandler = startHandler;
        return this;
    }

    @Override
    public ActionBuilder setEndHandler(Consumer<GameContext> endHandler) {
        this.endHandler = endHandler;
        return this;
    }

    @Override
    public Action build() {
        if (startHandler != null && endHandler != null) {
            new ActionBase.Keepable(actionHandler, startHandler, endHandler).setRegistryName(path);
        }
        return new ActionBase(actionHandler).setRegistryName(path);
    }
}