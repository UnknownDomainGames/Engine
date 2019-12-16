package nullengine.client.gui.event;

public interface EventHandler<T extends Event> {

    void onEvent(T event);
}
