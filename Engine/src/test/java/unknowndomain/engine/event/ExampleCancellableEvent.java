package unknowndomain.engine.event;

import unknowndomain.engine.event.Event.Cancellable;

public class ExampleCancellableEvent implements Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
