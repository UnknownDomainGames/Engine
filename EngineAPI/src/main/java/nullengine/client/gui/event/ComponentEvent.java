package nullengine.client.gui.event;

import nullengine.client.gui.Component;
import nullengine.event.Event;

public class ComponentEvent implements Event {
    private Component component;

    private ComponentEvent() {
    }

    public ComponentEvent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
