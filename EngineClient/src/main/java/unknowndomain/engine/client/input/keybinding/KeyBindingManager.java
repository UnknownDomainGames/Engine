package unknowndomain.engine.client.input.keybinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import unknowndomain.engine.Tickable;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.registry.Registry;

/**
 * Handles the registration of KeyBinding and also handles key inputs (and mouse
 * inputs)
 * 
 * @author Mouse0w0 and iTNTPiston
 *
 */
public class KeyBindingManager implements Tickable {
    /** Mappes the key binding index to the KeyBinding objects. */
    private final Multimap<Integer, KeyBinding> indexToBinding = HashMultimap.create();
    /** KeyBinding Registry */
    private final Registry<KeyBinding> registry;
    private final GameContext gameContext;
    /**
     * @Deprecated Not used.
     */
    @Deprecated
    private final Set<Key> pressedKey = new HashSet<>();
    /**
     * @Deprecated Not used.
     */
    @Deprecated
    private ActionManager actionManager;

    public KeyBindingManager(GameContext context, Registry<KeyBinding> keyBindingRegistry) {
        registry = keyBindingRegistry;
        gameContext = context;
    }

    /**
     * Register a KeyBinding
     * 
     * @param keybinding key binding to register
     */
    public void register(KeyBinding keybinding) {
        registry.register(keybinding);
        int code = keybinding.getCode().code;
        byte mods = KeyModifier.getCode(keybinding.getModifier());
        indexToBinding.put(getIndex(code, mods), keybinding);
    }

    protected void handlePress(int code, int modifiers) {
        Key key = Key.valueOf(code);
        pressedKey.add(key);
        Collection<KeyBinding> keyBindings = this.indexToBinding.get(getIndex(code, modifiers));
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
        Collection<KeyBinding> keyBindings = this.indexToBinding.get(getIndex(code, modifiers));
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

    /**
     * Handles Mouse and Key inputs
     */
    @Override
    public void tick() {
        for (KeyBinding keyBinding : indexToBinding.values()) {
            if (keyBinding.isActive() != keyBinding.isPressed()) {
                // state change
                keyBinding.setActive(keyBinding.isPressed());
                if (keyBinding.isActive()) {
                    keyBinding.onKeyStart(gameContext);
                } else {
                    keyBinding.onKeyEnd(gameContext);
                }
            } else if (keyBinding.isActive()) {
                // keep key
                keyBinding.onKeyKeep(gameContext);
            }
        }
    }

}
