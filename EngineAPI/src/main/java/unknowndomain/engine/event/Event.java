package unknowndomain.engine.event;

public interface Event {

	default boolean isCancellable() {
		return this instanceof Cancellable;
	}
}
