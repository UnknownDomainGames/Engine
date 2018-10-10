package unknowndomain.engine.client.gui.renderer;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Graphics;

public abstract class ComponentRenderer<C extends Component> {

    private final C component;

    public ComponentRenderer(C component) {
        this.component = component;
    }

    public C getComponent() {
        return component;
    }

    abstract public void render(Graphics graphics);

    public void dispose() {}
}
