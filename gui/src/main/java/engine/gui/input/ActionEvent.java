package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;

public class ActionEvent extends Event {

    public static final EventType<ActionEvent> ANY = new EventType<>("ACTION", EventType.ROOT);
    public static final EventType<ActionEvent> ACTION = ANY;

    public ActionEvent(EventType<? extends Event> eventType, EventTarget target) {
        super(eventType, target);
    }
}
