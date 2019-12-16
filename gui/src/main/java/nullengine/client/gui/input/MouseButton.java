package nullengine.client.gui.input;

public enum MouseButton {

    MOUSE_BUTTON_PRIMARY(0),
    MOUSE_BUTTON_SECONDARY(1),
    MOUSE_BUTTON_MIDDLE(2),
    MOUSE_BUTTON_4(3),
    MOUSE_BUTTON_5(4),
    MOUSE_BUTTON_6(5),
    MOUSE_BUTTON_7(6),
    MOUSE_BUTTON_8(7);

    private final int button;

    public static MouseButton valueOf(int button) {
        return MouseButton.values()[button];
    }

    MouseButton(int button) {
        this.button = button;
    }
}
