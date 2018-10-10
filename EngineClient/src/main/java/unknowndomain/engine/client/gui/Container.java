package unknowndomain.engine.client.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Container extends Component {

    private final List<Component> children = new LinkedList<>();
    private final List<Component> unmodifiableChildren = Collections.unmodifiableList(children);

    protected List<Component> getChildren() {
        return children;
    }

    public final List<Component> getUnmodifiableChildren() {
        return unmodifiableChildren;
    }

    abstract public void layoutChildren();
}
