package nullengine.client.gui.event;

import nullengine.client.gui.Component;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;

public class KeyEvent extends ComponentEvent {
    private Key key;
    private ActionMode mode;
    private KeyModifier modifiers;

    public KeyEvent(Component component, Key key, ActionMode mode, KeyModifier modifier) {
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

    public KeyModifier getModifier() {
        return modifiers;
    }

    public static class KeyDownEvent extends KeyEvent {

        public KeyDownEvent(Component component, Key key, ActionMode mode, KeyModifier modifier) {
            super(component, key, mode, modifier);
        }
    }

    public static class KeyHoldEvent extends KeyEvent {

        public KeyHoldEvent(Component component, Key key, ActionMode mode, KeyModifier modifier) {
            super(component, key, mode, modifier);
        }
    }

    public static class KeyUpEvent extends KeyEvent {

        public KeyUpEvent(Component component, Key key, ActionMode mode, KeyModifier modifier) {
            super(component, key, mode, modifier);
        }
    }
}
