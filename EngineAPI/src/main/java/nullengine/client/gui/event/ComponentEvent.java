package nullengine.client.gui.event;

import nullengine.client.gui.Node;
import nullengine.event.Event;

public class ComponentEvent implements Event {
    private Node node;

    private ComponentEvent() {
    }

    public ComponentEvent(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
