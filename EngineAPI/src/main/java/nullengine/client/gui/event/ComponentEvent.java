package nullengine.client.gui.event;

import nullengine.client.gui.Node;

public class ComponentEvent extends Event {
    public static final EventType<ComponentEvent> TYPE = new EventType<>("ComponentEvent");

    public ComponentEvent(Node source) {
        super(TYPE, source);
    }

    public ComponentEvent(EventType<? extends ComponentEvent> type, Node source) {
        super(type, source);
    }
}
