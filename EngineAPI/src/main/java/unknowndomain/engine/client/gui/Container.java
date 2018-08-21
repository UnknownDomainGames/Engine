package unknowndomain.engine.client.gui;

import java.util.LinkedList;
import java.util.List;

public class Container {

    private final List<Component> children = new LinkedList<>();

    public List<Component> getChildren() {
        return children;
    }
}
