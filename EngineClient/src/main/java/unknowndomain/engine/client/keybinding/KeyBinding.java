package unknowndomain.engine.client.keybinding;

import java.util.function.Consumer;

import unknowndomain.engine.client.keybinding.ActionMode;
import unknowndomain.engine.client.keybinding.KeyCode;
import unknowndomain.engine.client.keybinding.KeyModifier;
import unknowndomain.engine.registry.RegistryEntry;

public class KeyBinding extends RegistryEntry.Impl<KeyBinding> {

    private final KeyCode defaultCode;
    private final KeyModifier[] defaultMods;
    private final ActionMode defaultActionMode;
    private KeyCode code;
    private KeyModifier[] mods;
    private ActionMode actionMode;
    private boolean actived = false;

    public KeyBinding(KeyCode code) {
        this(code, ActionMode.PRESS, KeyModifier.EMPTY);
    }

    public KeyBinding(KeyCode code, ActionMode actionMode) {
        this(code, actionMode, KeyModifier.EMPTY);
    }

    public KeyBinding(KeyCode code, KeyModifier... keyMods) {
        this(code, ActionMode.PRESS, keyMods);
    }

    public KeyBinding(KeyCode code, ActionMode actionMode, KeyModifier... keyMods) {
        this.defaultCode = code != null ? code : KeyCode.KEY_UNKNOWN;
        this.defaultActionMode = actionMode != null ? actionMode : ActionMode.PRESS;
        this.defaultMods = keyMods;
        setCode(defaultCode);
        setModifier(defaultMods);
        setActionMode(defaultActionMode);
    }

    public static KeyBinding create(KeyCode code, ActionMode actionMode, Consumer<Void> onActive,
                                    Consumer<Void> onInactive, KeyModifier... keyMods) {
        return new KeyBinding(code, actionMode, keyMods) {
            @Override
            public void onActive() {
                if (onActive != null)
                    onActive.accept(null);
            }

            public void onKeep() {

            }

            @Override
            public void onInactive() {
                if (onInactive != null)
                    onInactive.accept(null);
            }
        };
    }

    public KeyCode getDefaultCode() {
        return defaultCode;
    }

    public KeyModifier[] getDefaultMods() {
        return defaultMods;
    }

    public ActionMode getDefaultActionMode() {
        return defaultActionMode;
    }

    public KeyCode getCode() {
        return code;
    }

    public void setCode(KeyCode code) {
        this.code = code != null ? code : KeyCode.KEY_UNKNOWN;
    }

    public KeyModifier[] getModifier() {
        return mods;
    }

    public void setModifier(KeyModifier... mods) {
        this.mods = mods;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode != null ? actionMode : ActionMode.PRESS;
    }

    public boolean isActived() {
        return actived;
    }

    private void setActived(boolean actived) {
        this.actived = actived;
        if (actived)
            onActive();
        else
            onInactive();
    }

    public void handleKey(boolean pressed) {
        if (getActionMode() == ActionMode.SWITCH && pressed) {
            setActived(!isActived());
        } else {
            setActived(pressed);
        }
    }

    public void onActive() {
    }

    public void onKeep() {

    }

    public void onInactive() {
    }
}
