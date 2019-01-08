package unknowndomain.engine.event;

public interface Event {

    default boolean isCancellable() {
        return this instanceof Cancellable;
    }

    public interface Cancellable extends Event {

        boolean isCancelled();

        void setCancelled(boolean cancelled);
    }
}
