package unknowndomain.engine.api.event;

public interface Event {

	default boolean isCancellable() {
		return this instanceof Cancellable;
	}
}
