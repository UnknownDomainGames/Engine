package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

public class KeyBinding {

	private KeyCode code;
	private KeyMode[] mode;
	private int modeCode;
	private ActiveMode activeMode;
	
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

	public ActiveMode getActiveMode() {
		return activeMode;
	}

	public void setActiveMode(ActiveMode activeMode) {
		this.activeMode = activeMode;
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
