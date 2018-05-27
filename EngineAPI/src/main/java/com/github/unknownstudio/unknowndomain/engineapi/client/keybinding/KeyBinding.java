package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

public class KeyBinding {

	private Key primaryKey;
	private KeyMode[] primaryKeyMode;
	
	private Key secondaryKey;
	private KeyMode[] secondaryKeyMode;
	
	private Action action = Action.RELEASED;
	private long pressedTime;
	
	public Key getPrimaryKey() {
		return primaryKey;
	}
	
	public void setPrimaryKey(Key primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public KeyMode[] getPrimaryKeyMode() {
		return primaryKeyMode;
	}
	
	public void setPrimaryKeyMode(KeyMode[] primaryKeyMode) {
		this.primaryKeyMode = primaryKeyMode;
	}
	
	public Key getSecondaryKey() {
		return secondaryKey;
	}
	
	public void setSecondaryKey(Key secondaryKey) {
		this.secondaryKey = secondaryKey;
	}
	
	public KeyMode[] getSecondaryKeyMode() {
		return secondaryKeyMode;
	}
	
	public void setSecondaryKeyMode(KeyMode[] secondaryKeyMode) {
		this.secondaryKeyMode = secondaryKeyMode;
	}
}
