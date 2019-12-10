package nullengine.client.gui.event.type;

import nullengine.client.gui.Node;
import nullengine.client.gui.event.EventType;

public class FocusEvent extends ComponentEvent {
    public static final EventType<FocusEvent> TYPE = new EventType<>("FocusEvent", ComponentEvent.TYPE);

    public FocusEvent(Node source) {
        super(TYPE,source);
    }

    public FocusEvent(EventType<? extends FocusEvent> type, Node source) {
        super(type, source);
    }

    public static class FocusGainEvent extends FocusEvent {
        public static final EventType<FocusGainEvent> TYPE = new EventType<>("FocusGainEvent", FocusEvent.TYPE);

        public FocusGainEvent(Node node) {
            super(TYPE,node);
        }
    }

    public static class FocusLostEvent extends FocusEvent {
        public static final EventType<FocusLostEvent> TYPE = new EventType<>("FocusLostEvent", FocusEvent.TYPE);

        public FocusLostEvent(Node node) {
            super(TYPE,node);
        }
    }
}
