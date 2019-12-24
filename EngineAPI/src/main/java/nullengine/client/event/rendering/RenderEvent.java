package nullengine.client.event.rendering;

import nullengine.event.Event;

public abstract class RenderEvent implements Event {

    private RenderEvent() {
    }

    public static final class Pre extends RenderEvent {

    }

    public static final class Post extends RenderEvent {

    }
}
