package unknowndomain.engine.client.input.keybinding;

/**
 * Handles the state of a single KeyBinding
 *
 */
public class KeyBinding {
    private final Key code;
    private final KeyModifier[] mods;
    private final ActionMode actionMode;
    private final String target;
    private boolean active = false;
    private boolean pressed = false;

    private KeyBinding(String target, Key code, ActionMode actionMode, KeyModifier... keyMods) {
        this.target = target;
        this.code = code;
        this.actionMode = actionMode;
        this.mods = keyMods;
    }

    public static KeyBinding create(String target, Key code, ActionMode actionMode, KeyModifier... keyMods) {
        return new KeyBinding(target, code == null ? Key.KEY_UNKNOWN : code,
                actionMode != null ? actionMode : ActionMode.PRESS,
                keyMods == null || keyMods.length == 0 ? KeyModifier.EMPTY : keyMods);
    }

    public String getTarget() {
        return target;
    }

    public Key getCode() {
        return code;
    }

    public KeyModifier[] getModifier() {
        return mods;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
