package unknowndomain.engine.client.gui.event;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.event.Event;

public class ComponentEvent implements Event {
    private Component component;

    private ComponentEvent(){}

    public ComponentEvent(Component component){
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
