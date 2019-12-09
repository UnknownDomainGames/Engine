package nullengine.client.gui.event;

import nullengine.client.gui.Node;

public class CharEvent extends ComponentEvent {
    private char c;

    public CharEvent(Node node, char c) {
        super(node);
        this.c = c;
    }

    public char getCharacter() {
        return c;
    }
}
