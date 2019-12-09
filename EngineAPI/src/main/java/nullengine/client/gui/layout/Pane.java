package nullengine.client.gui.layout;

import com.github.mouse0w0.observable.collection.ObservableList;
import nullengine.client.gui.Node;
import nullengine.client.gui.Region;

public class Pane extends Region {

    public static void setProperty(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.parent().getValue() != null) {
            node.requestParentLayout();
        }
    }

    public static Object getProperty(Node node, Object key) {
        if (node.hasProperties()) {
            return node.getProperties().get(key);
        }
        return null;
    }

    @Override
    public final ObservableList<Node> getChildren() {
        return super.getChildren();
    }
}
