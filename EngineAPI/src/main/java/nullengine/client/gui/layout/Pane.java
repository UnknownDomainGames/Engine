package nullengine.client.gui.layout;

import com.github.mouse0w0.lib4j.observable.collection.ObservableList;
import nullengine.client.gui.Component;
import nullengine.client.gui.Region;

public class Pane extends Region {

    public static void setProperty(Component component, Object key, Object value) {
        if (value == null) {
            component.getProperties().remove(key);
        } else {
            component.getProperties().put(key, value);
        }
        if (component.parent().getValue() != null) {
            component.requestParentLayout();
        }
    }

    public static Object getProperty(Component component, Object key) {
        if (component.hasProperties()) {
            return component.getProperties().get(key);
        }
        return null;
    }

    @Override
    public final ObservableList<Component> getChildren() {
        return super.getChildren();
    }
}
