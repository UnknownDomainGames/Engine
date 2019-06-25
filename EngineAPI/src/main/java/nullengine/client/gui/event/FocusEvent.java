package nullengine.client.gui.event;

import nullengine.client.gui.Component;

public class FocusEvent extends ComponentEvent {
    protected FocusEvent(Component component) {
        super(component);
    }

    public static class FocusGainEvent extends FocusEvent {
        public FocusGainEvent(Component component) {
            super(component);
        }
    }

    public static class FocusLostEvent extends FocusEvent {
        public FocusLostEvent(Component component) {
            super(component);
        }
    }
}
