package unknowndomain.engine.client.keybinding;

public class KeyBinding {
    private final KeyCode code;
    private final KeyModifier[] mods;
    private final ActionMode actionMode;
    private final String target;
    private boolean active = false;
    private boolean valid = true;

    private KeyBinding(String target, KeyCode code, ActionMode actionMode, KeyModifier... keyMods) {
        this.target = target;
        this.code = code;
        this.actionMode = actionMode;
        this.mods = keyMods;
    }

    public static KeyBinding create(String target, KeyCode code, ActionMode actionMode, KeyModifier... keyMods) {
        return new KeyBinding(target,
                code == null ? KeyCode.KEY_UNKNOWN : code,
                actionMode != null ? actionMode : ActionMode.PRESS,
                keyMods == null ? KeyModifier.EMPTY : keyMods);
    }

    public String getTarget() {
        return target;
    }

    public KeyCode getCode() {
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

    private void setActive(boolean active) {
        this.active = active;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

