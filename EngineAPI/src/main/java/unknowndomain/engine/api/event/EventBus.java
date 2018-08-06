package unknowndomain.engine.api.event;

public interface EventBus {

	/**
	 * @param event
	 * @return True if cancelled, false if not.
	 */
	boolean post(Event event);
	
	void register(Object listener);
	
	void unregister(Object listener);
}
