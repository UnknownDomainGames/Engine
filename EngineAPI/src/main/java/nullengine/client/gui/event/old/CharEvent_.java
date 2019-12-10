package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;

@Deprecated
public class CharEvent_ extends ComponentEvent_ {
    private char c;

    public CharEvent_(Node node, char c) {
        super(node);
        this.c = c;
    }

    public char getCharacter() {
        return c;
    }
}
