package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;
import nullengine.event.Event;

@Deprecated
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
