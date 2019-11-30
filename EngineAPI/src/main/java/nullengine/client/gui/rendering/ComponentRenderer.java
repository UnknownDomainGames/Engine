package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;

public interface ComponentRenderer<E extends Component> {

    void render(E component, Graphics graphics);
}
