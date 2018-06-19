package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

public enum KeyMode {

	SHIFT(0x01),
	CONTROL(0x02),
	ALT(0x04);
	
	private final int code;
	private KeyMode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static int getCode(KeyMode... keyModes) {
		int code = 0;
		for(KeyMode mode : keyModes) {
			code |= mode.getCode();
		}
		return code;
	}
}
