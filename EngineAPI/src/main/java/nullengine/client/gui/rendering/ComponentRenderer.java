package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;
import nullengine.client.rendering.RenderManager;

public interface ComponentRenderer<E extends Component> {

    void render(E component, Graphics graphics, RenderManager context);
}
