package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.rendering.RenderContext;

public interface ComponentRenderer<E extends Component> {

    void render(E component, Graphics graphics, RenderContext context);
}
