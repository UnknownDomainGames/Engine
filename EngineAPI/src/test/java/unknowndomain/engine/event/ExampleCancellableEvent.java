package unknowndomain.engine.event;

import unknowndomain.engine.event.Event;

public class ExampleCancellableEvent implements Event, Cancellable {
	
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
