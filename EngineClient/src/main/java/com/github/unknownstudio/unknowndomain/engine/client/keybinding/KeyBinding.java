package com.github.unknownstudio.unknowndomain.engine.client.keybinding;

import com.github.unknownstudio.unknowndomain.engineapi.keybinding.ActionMode;
import com.github.unknownstudio.unknowndomain.engineapi.keybinding.KeyCode;
import com.github.unknownstudio.unknowndomain.engineapi.keybinding.KeyModifier;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class KeyBinding extends RegistryEntry.Impl<KeyBinding> {

	private final KeyCode defaultCode;
	private final KeyModifier[] defaultMods;
	private final ActionMode defaultActionMode;
	
	private KeyCode code;
	private KeyModifier[] mods;
	private ActionMode actionMode;
	
	private boolean actived = false;
	private long lastStateChangeTime;
	
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
	
	public long getLastStateChangeTime() {
		return System.currentTimeMillis() - lastStateChangeTime;
	}
	
	void setPressed(boolean pressed) {
		actived = pressed;
		lastStateChangeTime = System.currentTimeMillis();
		if(pressed)
			onActive();
		else 
			onInactive();
	}

	public void onActive() {
	}
	
	public void onInactive() {
	}
}
