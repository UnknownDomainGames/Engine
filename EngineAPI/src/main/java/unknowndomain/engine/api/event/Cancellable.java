package unknowndomain.engine.api.event;

public interface Cancellable {
	
	boolean isCancelled();
	
	void setCancelled(boolean cancelled);
}
