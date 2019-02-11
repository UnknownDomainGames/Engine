package unknowndomain.engine.client.gui.event;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.input.keybinding.KeyModifier;
import unknowndomain.engine.event.Event;

public class KeyEvent extends ComponentEvent {
    private Key key;
    private ActionMode mode;
    private KeyModifier[] modifiers;

    public KeyEvent(Component component, Key key, ActionMode mode, KeyModifier[] modifier) {
        super(component);
        this.key = key;
        this.mode = mode;
        this.modifiers = modifier;
    }

    public Key getKey() {
        return key;
    }

    public ActionMode getMode() {
        return mode;
    }

    public static class KeyDownEvent extends KeyEvent{

        public KeyDownEvent(Component component, Key key, ActionMode mode, KeyModifier[] modifier) {
            super(component, key, mode, modifier);
        }
    }

    public static class KeyHoldEvent extends KeyEvent{

        public KeyHoldEvent(Component component, Key key, ActionMode mode, KeyModifier[] modifier) {
            super(component, key, mode, modifier);
        }
    }

    public static class KeyUpEvent extends KeyEvent{

        public KeyUpEvent(Component component, Key key, ActionMode mode, KeyModifier[] modifier) {
            super(component, key, mode, modifier);
        }
    }
}
