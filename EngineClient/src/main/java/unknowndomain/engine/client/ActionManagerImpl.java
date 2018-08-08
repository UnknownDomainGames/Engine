package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.registry.SimpleRegistry;

import java.util.HashMap;
import java.util.Map;

public class ActionManagerImpl extends SimpleRegistry<Action> implements ActionManager {
    private Map<ResourcePath, ActionRuntime> runningAction = new HashMap<>();
    private GameContext context;

    public ActionManagerImpl(GameContext context) {
        this.context = context;
    }

    public void tick() {
        for (ActionRuntime runtime : runningAction.values()) {
            runtime.action.onActionKeep(context, runtime.tickElapsed++);
        }
    }

    @Override
    public void start(ResourcePath action) {
        if (runningAction.containsKey(action)) return;

        Action value = getValue(action);
        if (value != null) {
            value.onActionStart(context);
            if (value instanceof Action.Keepable) {
                runningAction.put(action, new ActionRuntime(((Action.Keepable) value)));
            }
        }
    }

    public void end(ResourcePath action) {
        ActionRuntime value = runningAction.get(action);
        if (value != null) {
            value.action.onActionEnd(context, value.tickElapsed);
            runningAction.remove(action);
        }
    }

    private class ActionRuntime {
        private Action.Keepable action;
        private int tickElapsed;

        ActionRuntime(Action.Keepable action) {
            this.action = action;
            this.tickElapsed = 0;
        }
    }
}
