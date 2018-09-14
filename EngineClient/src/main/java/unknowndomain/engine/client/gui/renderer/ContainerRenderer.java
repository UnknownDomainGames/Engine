package unknowndomain.engine.client.gui.renderer;

import unknowndomain.engine.client.gui.Container;

public abstract class ContainerRenderer<C extends Container> extends ComponentRenderer<C> {

    public ContainerRenderer(C component) {
        super(component);
    }
}
