package nullengine.client.gui.event.type;

import nullengine.client.gui.Node;
import nullengine.client.gui.event.EventType;

public class CharEvent extends ComponentEvent {
    public static final EventType<CharEvent> TYPE = new EventType<>("CharEvent", ComponentEvent.TYPE);

    private char c;

    public CharEvent(EventType<? extends CharEvent> type, Node source, char c) {
        super(type, source);
    }

    public CharEvent(Node node, char c) {
        this(TYPE, node, c);
    }

    public char getCharacter() {
        return c;
    }
}
