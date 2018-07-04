package com.github.unknownstudio.unknowndomain.engine.client.keybinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.unknownstudio.unknowndomain.engineapi.keybinding.KeyCode;
import com.github.unknownstudio.unknowndomain.engineapi.keybinding.KeyModifier;
import com.github.unknownstudio.unknowndomain.engineapi.registry.SimpleRegistry;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class ClientKeyBindingManager extends SimpleRegistry<KeyBinding>{

	private final Multimap<Integer, KeyBinding> codeToKeyBinding = LinkedListMultimap.create();
	
	private final Set<KeyCode> pressedKey = new HashSet<>();
	private KeyModifier[] pressedMods;
	
	public ClientKeyBindingManager() {}
	
	public void update() {
		codeToKeyBinding.clear();
		for(KeyBinding keyBinding : getValues()) {
			if(keyBinding.getCode() != KeyCode.KEY_UNKNOWN) {
				codeToKeyBinding.put(keyBinding.getCode().code | (KeyModifier.getCode(keyBinding.getModifier()) << 9), keyBinding);
			}
		}
	}
	
	public void handlePress(int code, int mods) {
		KeyCode keyCode = KeyCode.valueOf(code);
		KeyModifier[] keyMods = KeyModifier.valueOf(mods);
		pressedKey.add(keyCode);
		pressedMods = keyMods;
		Collection<KeyBinding> keyBindings = codeToKeyBinding.get(keyCode.code | ((mods & 0x07) << 9));
		for(KeyBinding keyBinding : keyBindings) {
			keyBinding.handleKey(true);
		}
	}
	
	public void handleRelease(int code, int mods) {
		KeyCode keyCode = KeyCode.valueOf(code);
		KeyModifier[] keyMods = KeyModifier.valueOf(mods);
		pressedKey.remove(keyCode);
		pressedMods = keyMods;
		Collection<KeyBinding> keyBindings = codeToKeyBinding.get(keyCode.code | ((mods & 0x07) << 9));
		for(KeyBinding keyBinding : keyBindings) {
			keyBinding.handleKey(false);
		}
	}
	
}
