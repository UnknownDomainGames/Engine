package unknowndomain.engine.client.keybinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import unknowndomain.engine.api.keybinding.KeyCode;
import unknowndomain.engine.api.keybinding.KeyModifier;
import unknowndomain.engine.api.registry.SimpleRegistry;

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
			System.out.println("HELLO");
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
