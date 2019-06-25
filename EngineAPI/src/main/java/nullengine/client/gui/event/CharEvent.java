package nullengine.client.gui.event;

import nullengine.client.gui.Component;

public class CharEvent extends ComponentEvent {
    private char c;

    public CharEvent(Component component, char c) {
        super(component);
        this.c = c;
    }

    public char getCharacter() {
        return c;
    }
}
