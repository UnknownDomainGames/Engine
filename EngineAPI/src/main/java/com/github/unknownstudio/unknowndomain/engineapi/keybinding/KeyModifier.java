package com.github.unknownstudio.unknowndomain.engineapi.keybinding;

public enum KeyModifier {

	SHIFT(0x01),
	CONTROL(0x02),
	ALT(0x04);
	
	private final byte code;
	private KeyModifier(int code) {
		this.code = (byte) code;
	}
	
	public byte getCode() {
		return code;
	}
	
	public static byte getCode(KeyModifier... keyMods) {
		byte code = 0;
		for (KeyModifier mod : keyMods) {
			code |= mod.getCode();
		}
		return code;
	}
	
	public static final KeyModifier[] EMPTY = new KeyModifier[0];
	public static KeyModifier[] valueOf(int mods) {
		switch (mods & 0x07) {
		case 0:
			return EMPTY;
		case 1:
			return new KeyModifier[] {SHIFT};
		case 2:
			return new KeyModifier[] {CONTROL};
		case 3:
			return new KeyModifier[] {SHIFT, CONTROL};
		case 4:
			return new KeyModifier[] {ALT};
		case 5:
			return new KeyModifier[] {SHIFT, ALT};
		case 6:
			return new KeyModifier[] {CONTROL, ALT};
		case 7:
			return new KeyModifier[] {SHIFT, CONTROL, ALT};
		default:
			return EMPTY;
		}
	}
}
