package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.registry.RegisterException;
import unknowndomain.engine.registry.Registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionManagerImpl implements ActionManager {
    private Map<String, ActionRuntime> runningAction = new HashMap<>();

    private GameContext context;
    private Registry<Action> delegate;

    public ActionManagerImpl(GameContext context, Registry<Action> delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void tick() {
        for (ActionRuntime runtime : runningAction.values()) {
            runtime.action.onActionKeep(context, runtime.tickElapsed);
            ++runtime.tickElapsed;
        }
    }

    @Override
    public Class<Action> getRegistryEntryType() {
        return delegate.getRegistryEntryType();
    }

    @Override
    public Action register(Action obj) throws RegisterException {
        return delegate.register(obj);
    }

    @Override
    public Action getValue(String registryName) {
        return delegate.getValue(registryName);
    }

    @Override
    public String getKey(Action value) {
        return delegate.getKey(value);
    }

    @Override
    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Action value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<String> getKeys() {
        return delegate.getKeys();
    }

    @Override
    public Collection<Action> getValues() {
        return delegate.getValues();
    }

    @Override
    public int getId(Action obj) {
        return delegate.getId(obj);
    }

    @Override
    public int getId(String key) {
        return delegate.getId(key);
    }

    @Override
    public String getKey(int id) {
        return delegate.getKey(id);
    }

    @Override
    public Action getValue(int id) {
        return delegate.getValue(id);
    }

    @Override
    public Collection<Map.Entry<String, Action>> getEntries() {
        return delegate.getEntries();
    }

    @Override
    public void start(String action) {
        if (runningAction.containsKey(action)) return;

        Action value = getValue(action);
        if (value != null) {
            value.onActionStart(context);
            if (value instanceof Action.Keepable) {
                runningAction.put(action, new ActionRuntime(((Action.Keepable) value)));
            }
        }
    }

    public void end(String action) {
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
