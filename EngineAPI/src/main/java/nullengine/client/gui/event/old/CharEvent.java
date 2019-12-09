package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;

@Deprecated
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
