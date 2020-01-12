package nullengine.client.gui.input;

import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventTarget;
import nullengine.client.gui.event.EventType;

public class ActionEvent extends Event {

    public static final EventType<ActionEvent> ANY = new EventType<>("ACTION", EventType.ROOT);
    public static final EventType<ActionEvent> ACTION = ANY;

    public ActionEvent(EventType<? extends Event> eventType, EventTarget target) {
        super(eventType, target);
    }
}
