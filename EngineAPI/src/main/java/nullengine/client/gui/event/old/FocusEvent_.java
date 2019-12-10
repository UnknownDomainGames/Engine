package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;

@Deprecated
public class FocusEvent_ extends ComponentEvent_ {
    protected FocusEvent_(Node node) {
        super(node);
    }

    public static class FocusGainEvent extends FocusEvent_ {
        public FocusGainEvent(Node node) {
            super(node);
        }
    }

    public static class FocusLostEvent extends FocusEvent_ {
        public FocusLostEvent(Node node) {
            super(node);
        }
    }
}
