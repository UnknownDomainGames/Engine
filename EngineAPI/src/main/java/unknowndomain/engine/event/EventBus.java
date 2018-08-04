package unknowndomain.engine.event;

public interface EventBus {

	void post(Event event);
	
	void register(Object subscriber);
	
	void unregister(Object subscriber);
}
