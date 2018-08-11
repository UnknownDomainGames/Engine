package unknowndomain.engine.client.keybinding;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import unknowndomain.engine.client.UnknownDomain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KeyBindingManager {
    private final Multimap<Integer, KeyBinding> codeToBinding = LinkedListMultimap.create();

    private final Set<KeyCode> pressedKey = new HashSet<>();

    public void add(KeyBinding keybinding) {
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        int store = code | ((mods & 0x07) << 9);
        codeToBinding.put(store, keybinding);
    }

    public void remove(KeyBinding keybinding) {
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        int store = code | ((mods & 0x07) << 9);
        codeToBinding.remove(store, keybinding);
    }

    public void handlePress(int code, int mods) {
        KeyCode keyCode = KeyCode.valueOf(code);
        pressedKey.add(keyCode);
        Collection<KeyBinding> keyBindings = codeToBinding.get(keyCode.code | ((mods & 0x07) << 9));
        for (KeyBinding binding : keyBindings) {
            for (KeyModifier modifier : binding.getModifier()) {

            }
            UnknownDomain.getEngine().getActionManager().start(binding.getTarget());
        }
    }

    public void handleRelease(int code, int mods) {
        KeyCode keyCode = KeyCode.valueOf(code);
        pressedKey.add(keyCode);
        Collection<KeyBinding> keyBindings = codeToBinding.get(((mods & 0x07) << 9 | keyCode.code));
        for (KeyBinding binding : keyBindings) {
            UnknownDomain.getEngine().getActionManager().end(binding.getTarget());
        }
    }

}
