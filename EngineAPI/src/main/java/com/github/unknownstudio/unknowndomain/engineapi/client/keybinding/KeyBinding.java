package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class KeyBinding implements RegistryEntry<KeyBinding>{
	
	private String registryName;
	
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
	
	public long getPressedTime() {
		return pressedTime;
	}
	
	public void onPressed() {
	}
	
	public void onReleased() {
	}

	@Override
	public String getRegistryName() {
		return registryName;
	}

	@Override
	public void setRegistryName(String name) {
		registryName = name;
	}
}
