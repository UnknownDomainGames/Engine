package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

public class KeyBinding {
	
	private KeyCode code;
	private KeyBindingMode mode;
	
	private boolean pressed = false;
	private long pressedTime;
	
	public KeyCode getCode() {
		return code;
	}

	public void setCode(KeyCode code) {
		this.code = code;
	}
	
	public KeyBindingMode getMode() {
		return mode;
	}

	public void setMode(KeyBindingMode mode) {
		this.mode = mode;
	}

	public boolean isPressed() {
		return pressed;
	}
	
	public void onPressed() {
	}
	
	public void onReleased() {
	}
}
