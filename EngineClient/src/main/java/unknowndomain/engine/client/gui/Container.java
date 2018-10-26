package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.collection.ObservableCollections;
import com.github.mouse0w0.lib4j.observable.collection.ObservableList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Container extends Component {

    private final ObservableList<Component> children = ObservableCollections.observableList(new LinkedList<>());
    private final ObservableList<Component> unmodifiableChildren = ObservableCollections.unmodifiableObservableList(children);

    public Container() {
        children.addChangeListener(change -> {
            for (Component component : change.getAdded())
                component.parent = this;
            for (Component component : change.getRemoved())
                component.parent = null;
        });
    }

    protected ObservableList<Component> getChildren() {
        return children;
    }

    public final ObservableList<Component> getUnmodifiableChildren() {
        return unmodifiableChildren;
    }

    abstract public void layoutChildren();

    protected final void layoutInArea(Component component, int x, int y, int width, int height) {
        component.x = x;
        component.y = y;
        component.width = width;
        component.height = height;
    }
}
