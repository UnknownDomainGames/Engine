package unknowndomain.engine.client.keybinding;

import java.util.function.Consumer;

import unknowndomain.engine.api.keybinding.ActionMode;
import unknowndomain.engine.api.keybinding.KeyCode;
import unknowndomain.engine.api.keybinding.KeyModifier;
import unknowndomain.engine.api.registry.RegistryEntry;

public abstract class KeyBinding extends RegistryEntry.Impl<KeyBinding> {
	
	public static KeyBinding create(KeyCode code, ActionMode actionMode, Consumer<Void> onActive,
			Consumer<Void> onInactive, KeyModifier... keyMods) {
		return new KeyBinding(code, actionMode, keyMods) {
			@Override
			public void onActive() {
				if(onActive != null)
					onActive.accept(null);
			}
			
			@Override
			public void onInactive() {
				if(onInactive != null)
					onInactive.accept(null);
			}
		};
	}

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
	
	public void handleKey(boolean pressed) {
		if(getActionMode() == ActionMode.SWITCH && pressed) {
			setActived(!isActived());
		} else {
			setActived(pressed);
		}
	}
	
	private void setActived(boolean actived) {
		this.actived = actived;
		if(actived)
			onActive();
		else 
			onInactive();
	}

	public void onActive() {
	}
	
	public void onInactive() {
	}
}
