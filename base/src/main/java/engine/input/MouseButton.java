package engine.input;

public enum MouseButton {
    MOUSE_BUTTON_PRIMARY,
    MOUSE_BUTTON_SECONDARY,
    MOUSE_BUTTON_MIDDLE,
    MOUSE_BUTTON_4,
    MOUSE_BUTTON_5,
    MOUSE_BUTTON_6,
    MOUSE_BUTTON_7,
    MOUSE_BUTTON_8;

    private final static MouseButton[] VALUES = values();

    public static MouseButton valueOf(int button) {
        return VALUES[button];
    }
}
