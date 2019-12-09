package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;

@Deprecated
public class KeyEvent extends ComponentEvent {
    private Key key;
    private ActionMode mode;
    private KeyModifier modifiers;

    public KeyEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
        super(node);
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

        public KeyDownEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(node, key, mode, modifier);
        }
    }

    public static class KeyHoldEvent extends KeyEvent {

        public KeyHoldEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(node, key, mode, modifier);
        }
    }

    public static class KeyUpEvent extends KeyEvent {

        public KeyUpEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(node, key, mode, modifier);
        }
    }
}
