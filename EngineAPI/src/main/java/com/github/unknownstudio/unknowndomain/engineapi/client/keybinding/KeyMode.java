package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

public enum KeyMode {

	SHIFT(0x01),
	CONTROL(0x02),
	ALT(0x04),
	SUPER(0x08),
	CAPS_LOCK(0x10),
	NUM_LOCK(0x20);
	
	public final int code;
	
	KeyMode(int code) {
		this.code = code;
	}
}
