package nullengine.client.gui.input;

import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventTarget;
import nullengine.client.gui.event.EventType;
import nullengine.input.KeyCode;
import nullengine.input.Modifiers;

public class KeyEvent extends Event {
    public static final EventType<KeyEvent> ANY = new EventType<>("KEY");

    public static final EventType<KeyEvent> KEY = ANY;

    public static final EventType<KeyEvent> KEY_PRESSED = new EventType<>("KEY_PRESSED", ANY);

    public static final EventType<KeyEvent> KEY_RELEASED = new EventType<>("KEY_RELEASED", ANY);

    public static final EventType<KeyEvent> KEY_TYPED = new EventType<>("KEY_TYPED", ANY);

    private final KeyCode key;
    private final String character;
    private final Modifiers modifier;
    private final boolean pressed;

    public KeyEvent(EventType<? extends Event> eventType, EventTarget target, KeyCode key, Modifiers modifier, boolean pressed) {
        this(eventType, target, key, key.getCharacter(), modifier, pressed);
    }

    public KeyEvent(EventType<? extends Event> eventType, EventTarget target, KeyCode key, String character, Modifiers modifier, boolean pressed) {
        super(eventType, target);
        this.key = key;
        this.character = character;
        this.modifier = modifier;
        this.pressed = pressed;
    }

    public KeyCode getKey() {
        return key;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    public String getCharacter() {
        return character;
    }

    public boolean isPressed() {
        return pressed;
    }
}
