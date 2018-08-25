package unknowndomain.engine.client.keybinding;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.action.ActionManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KeyBindingManager {
    private final Multimap<Integer, KeyBinding> codeToBinding = LinkedListMultimap.create();

    private final Set<KeyCode> pressedKey = new HashSet<>();
    private final ActionManager actionManager;

    public KeyBindingManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public void add(KeyBinding keybinding) {
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        // int store = code | ((mods & 0x07) << 9);
        int store = code;
        codeToBinding.put(store, keybinding);
    }

    public void remove(KeyBinding keybinding) {
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        int store = code | ((mods & 0x07) << 9);
        codeToBinding.remove(store, keybinding);
    }

    private void handlePress(int code, int mods) {
        KeyCode keyCode = KeyCode.valueOf(code);
        pressedKey.add(keyCode);
        // Collection<KeyBinding> keyBindings = codeToBinding.get(keyCode.code | ((mods
        // & 0x07) << 9));
        Collection<KeyBinding> keyBindings = codeToBinding.get(keyCode.code);
        for (KeyBinding binding : keyBindings) {
            actionManager.start(binding.getTarget());
        }
    }

    private void handleRelease(int code, int mods) {
        KeyCode keyCode = KeyCode.valueOf(code);
        pressedKey.add(keyCode);
        // Collection<KeyBinding> keyBindings = codeToBinding.get(((mods & 0x07) << 9 |
        // keyCode.code));
        Collection<KeyBinding> keyBindings = codeToBinding.get(keyCode.code);
        for (KeyBinding binding : keyBindings) {
            actionManager.end(binding.getTarget());
        }
    }

    public void handleMousePress(int button, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                handlePress(button + 400, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                handleRelease(button + 400, modifiers);
                break;
            default:
                break;
        }
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                handlePress(key, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                handleRelease(key, modifiers);
                break;
            default:
                break;
        }
        // if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
        // if (paused) {
        // window.hideCursor();
        // paused = false;
        // } else {
        // window.showCursor();
        // paused = true;
        // }
        // }
    }
}
