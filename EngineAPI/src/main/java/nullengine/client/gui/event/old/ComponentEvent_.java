package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;
import nullengine.event.Event;

@Deprecated
public class ComponentEvent_ implements Event {
    private Node node;

    private ComponentEvent_() {
    }

    public ComponentEvent_(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
