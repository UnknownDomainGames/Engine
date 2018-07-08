package unknowndomain.engine.api.event;

public interface EventBus {

	void post(Event event);
	
	void register(Object subscriber);
	
	void unregister(Object subscriber);
}
