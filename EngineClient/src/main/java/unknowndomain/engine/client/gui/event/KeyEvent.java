package unknowndomain.engine.client.gui.event;

import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.event.Event;

public class KeyEvent implements Event {
    private Key key;
    private ActionMode mode;

    public KeyEvent(Key key, ActionMode mode) {
        this.key = key;
        this.mode = mode;
    }

    public static class KeyDownEvent extends KeyEvent{

        public KeyDownEvent(Key key, ActionMode mode) {
            super(key, mode);
        }
    }

    public static class KeyHoldEvent extends KeyEvent{

        public KeyHoldEvent(Key key, ActionMode mode) {
            super(key, mode);
        }
    }

    public static class KeyUpEvent extends KeyEvent{

        public KeyUpEvent(Key key, ActionMode mode) {
            super(key, mode);
        }
    }
}
