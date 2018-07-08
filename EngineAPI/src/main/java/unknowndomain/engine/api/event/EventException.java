package unknowndomain.engine.api.event;

public class EventException extends RuntimeException {
	
	public EventException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
