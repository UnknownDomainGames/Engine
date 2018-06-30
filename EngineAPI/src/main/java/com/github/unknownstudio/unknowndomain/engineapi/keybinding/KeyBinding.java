package com.github.unknownstudio.unknowndomain.engineapi.keybinding;

import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class KeyBinding extends RegistryEntry.Impl<KeyBinding> {

	private KeyCode defaultCode;
	private KeyMode[] defaultMode;
	private ActionMode[] defaultActionMode;
	
	private KeyCode code;
	private KeyMode[] mode;
	private int modeCode;
	private ActionMode actionMode;
	
	private boolean pressed = false;
	private long pressedTime;
	
	public KeyCode getCode() {
		return code;
	}

	public void setCode(KeyCode code) {
		this.code = code;
	}
	
	public KeyMode[] getMode() {
		return mode;
	}

	public void setMode(KeyMode... mode) {
		this.mode = mode;
		this.modeCode = KeyMode.getCode(mode);
	}
	
	public int getModeCode() {
		return modeCode;
	}

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}

	public boolean isPressed() {
		return pressed;
	}
	
	public long getPressedTime() {
		return pressedTime;
	}
	
	public void onPressed() {
	}
	
	public void onReleased() {
	}


}
