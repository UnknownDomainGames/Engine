package unknowndomain.engine.client.gui.event;

import unknowndomain.engine.event.Event;

public class CharEvent implements Event {
    private char c;

    public CharEvent(char c){
        this.c = c;
    }

    public char getCharacter(){
        return c;
    }
}
