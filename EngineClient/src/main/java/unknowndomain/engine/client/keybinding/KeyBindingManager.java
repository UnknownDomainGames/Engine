package unknowndomain.engine.client.keybinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import unknowndomain.engine.client.keybinding.KeyCode;
import unknowndomain.engine.client.keybinding.KeyModifier;
import unknowndomain.engine.registry.SimpleRegistry;
import unknowndomain.engine.client.resource.ResourceManager;

public class KeyBindingManager extends SimpleRegistry<KeyBinding> {
    private final Multimap<Integer, KeyBinding> codeToKeyBinding = LinkedListMultimap.create();
    private final Set<KeyCode> pressedKey = new HashSet<>();
    private ResourceManager resourceManager;
    private KeyModifier[] pressedMods;

    public KeyBindingManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void update() {
        codeToKeyBinding.clear();
        for (KeyBinding keyBinding : getValues()) {
            if (keyBinding.getCode() != KeyCode.KEY_UNKNOWN) {
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
        for (KeyBinding keyBinding : keyBindings) {
            keyBinding.handleKey(true);
        }
    }

    public void handleRelease(int code, int mods) {
        KeyCode keyCode = KeyCode.valueOf(code);
        KeyModifier[] keyMods = KeyModifier.valueOf(mods);
        pressedKey.remove(keyCode);
        pressedMods = keyMods;
        Collection<KeyBinding> keyBindings = codeToKeyBinding.get(keyCode.code | ((mods & 0x07) << 9));
        for (KeyBinding keyBinding : keyBindings) {
            keyBinding.handleKey(false);
        }
    }

}
