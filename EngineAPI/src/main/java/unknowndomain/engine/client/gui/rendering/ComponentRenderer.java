package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;

public interface ComponentRenderer<E extends Component> {

    void render(E component, Graphics graphics);
}
