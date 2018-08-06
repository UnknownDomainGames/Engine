package unknowndomain.engine.api.event;

public class EventException extends RuntimeException {
	
	public EventException() {
	}
	
	public EventException(String message) {
		super(message);
	}
	
	public EventException(Throwable throwable) {
		super(throwable);
	}
	
	public EventException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
