package nullengine.event;

/**
 * Every cancellable event class should implement this interface.
 */
public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
