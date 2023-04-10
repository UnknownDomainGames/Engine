package engine.client.event;

import engine.event.Event;

public abstract class ClientTickEvent implements Event {
    private ClientTickEvent() {
    }

    public static final class Pre extends ClientTickEvent {
    }

    public static final class Post extends ClientTickEvent {
    }
}
