package unknowndomain.engine.client.gui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Container extends Component {

    public abstract List<Component> getChildren();

    public abstract boolean addChild(Component component);

    public boolean addChildren(Component... components){
        return addChildren(Stream.of(components).collect(Collectors.toList()));
    }

    public abstract boolean addChildren(List<Component>... components);

}
