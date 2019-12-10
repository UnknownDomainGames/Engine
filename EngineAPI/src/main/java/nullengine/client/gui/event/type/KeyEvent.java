package nullengine.client.gui.event.type;

import nullengine.client.gui.Node;
import nullengine.client.gui.event.EventType;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;

public class KeyEvent extends ComponentEvent {
    public static final EventType<KeyEvent> TYPE = new EventType<>("KeyEvent", ComponentEvent.TYPE);

    private Key key;
    private ActionMode mode;
    private KeyModifier modifiers;

    public KeyEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
        this(TYPE, node, key, mode, modifier);
    }

    public KeyEvent(EventType<? extends KeyEvent> type, Node node, Key key, ActionMode mode, KeyModifier modifier) {
        super(type, node);
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
        public static final EventType<KeyDownEvent> TYPE = new EventType<>("KeyDownEvent", KeyEvent.TYPE);

        public KeyDownEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(TYPE, node, key, mode, modifier);
        }
    }

    public static class KeyHoldEvent extends KeyEvent {
        public static final EventType<KeyHoldEvent> TYPE = new EventType<>("KeyHoldEvent", KeyEvent.TYPE);

        public KeyHoldEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(TYPE, node, key, mode, modifier);
        }
    }

    public static class KeyUpEvent extends KeyEvent {
        public static final EventType<KeyUpEvent> TYPE = new EventType<>("KeyUpEvent", KeyEvent.TYPE);

        public KeyUpEvent(Node node, Key key, ActionMode mode, KeyModifier modifier) {
            super(TYPE, node, key, mode, modifier);
        }
    }
}
