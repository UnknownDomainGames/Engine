package unknowndomain.engine.client.input.keybinding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.Tickable;
import unknowndomain.engine.action.ActionManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KeyBindingManager implements Tickable {

    private final Multimap<Integer, KeyBinding> keyBindings = HashMultimap.create();
    /**
     * @Deprecated Not used.
     */
    @Deprecated
    private final Set<Key> pressedKey = new HashSet<>();

    private final ActionManager actionManager;

    public KeyBindingManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    /**
     * Register a KeyBinding
     * 
     * @param keybinding key binding to register
     */
    public void add(KeyBinding keybinding) {
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        keyBindings.put(getIndex(code, mods), keybinding);
    }

    protected void handlePress(int code, int modifiers) {
        Key key = Key.valueOf(code);
        pressedKey.add(key);
        Collection<KeyBinding> keyBindings = this.keyBindings.get(getIndex(code, modifiers));
        for (KeyBinding binding : keyBindings) {
            binding.setPressed(true);
        }
        // Trigger single key
        if (modifiers != 0) {
            handlePress(code, 0);
        }
    }

    protected void handleRelease(int code, int modifiers) {
        Key key = Key.valueOf(code);
        pressedKey.remove(key);
        Collection<KeyBinding> keyBindings = this.keyBindings.get(getIndex(code, modifiers));
        for (KeyBinding binding : keyBindings) {
            binding.setPressed(false);
        }
        // Trigger single key
        if (modifiers != 0) {
            handleRelease(code, 0);
        }
    }

    /**
     * Get the index of the modified key
     * 
     * @param code      Key code
     * @param modifiers Modifiers
     * @return An index value with last 4-bits presenting the modifiers
     */
    protected int getIndex(int code, int modifiers) {
        return modifiers + code << 4;
    }

    public void handleMouse(int button, int action, int modifiers) {
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

    public void handleKey(int key, int scancode, int action, int modifiers) {
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
    }

    @Override
    public void tick() {
        for (KeyBinding keyBinding : keyBindings.values()) {
            if (keyBinding.isActive() != keyBinding.isPressed()) {
                keyBinding.setActive(keyBinding.isPressed());
                if (keyBinding.isActive()) {
                    actionManager.start(keyBinding.getTarget());
                } else {
                    actionManager.end(keyBinding.getTarget());
                }
            }
        }
    }
}
