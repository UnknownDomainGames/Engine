package unknowndomain.engine.client.gui.renderer;

import unknowndomain.engine.client.gui.Component;

public abstract class ComponentRenderer<C extends Component> {

    private final C component;

    public ComponentRenderer(C component) {
        this.component = component;
    }

    public C getComponent() {
        return component;
    }

    public abstract void render();
}
