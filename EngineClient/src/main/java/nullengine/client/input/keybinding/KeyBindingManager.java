package nullengine.client.input.keybinding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import nullengine.client.EngineClient;
import nullengine.client.rendering.display.Window;
import nullengine.logic.Tickable;
import nullengine.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the registration of KeyBinding and also handles key inputs (and mouse
 * inputs)
 */
public class KeyBindingManager implements Tickable, KeyBindingConfig {

    private final EngineClient engineClient;
    /**
     * Mappes the key binding index to the KeyBinding objects.
     */
    private final Multimap<Integer, KeyBinding> indexToBinding = HashMultimap.create();
    /**
     * KeyBinding Registry
     */
    private final Registry<KeyBinding> registry;
    /**
     * @Deprecated Not used.
     */
    @Deprecated
    private final Set<Key> pressedKey = new HashSet<>();

    public KeyBindingManager(EngineClient engineClient, Registry<KeyBinding> keyBindingRegistry) {
        this.engineClient = engineClient;
        this.registry = keyBindingRegistry;
    }

    /**
     * Register a KeyBinding
     *
     * @param keybinding key binding to register
     * @Deprecated This should happen when listening to
     * EngineEvent.RegistrationStart
     */
    @Deprecated
    public void register(KeyBinding keybinding) {
        registry.register(keybinding);
    }

    /**
     * Reload the bindings. Use this after keybinding settings have changed
     */
    public void reload() {
        indexToBinding.clear();
        for (KeyBinding keybinding : registry.getValues()) {
            int code = keybinding.getKey().code;
            int mods = keybinding.getModifier().getInternalCode();
            indexToBinding.put(getIndex(code, mods), keybinding);
        }
    }

    protected void handlePress(int code, int modifiers) {
        Key key = Key.valueOf(code);
        pressedKey.add(key);
        Collection<KeyBinding> keyBindings = this.indexToBinding.get(getIndex(code, modifiers));
        for (KeyBinding binding : keyBindings) {
            if (engineClient.getRenderContext().getGuiManager().isDisplayingScreen() && !binding.isAllowInScreen())
                continue;
            binding.setPressed(true);
            binding.setDirty(true);
        }
        // Trigger single key
        if (modifiers != 0) {
            handlePress(code, 0);
        }
    }

    protected void handleRelease(int code, int modifiers) {
        // TODO: Remove it, hard code.
        Key key = Key.valueOf(code);
        pressedKey.remove(key);
        Collection<KeyBinding> keyBindings = this.indexToBinding.get(getIndex(code, modifiers));
        for (KeyBinding binding : keyBindings) {
            if (engineClient.getRenderContext().getGuiManager().isDisplayingScreen() && !binding.isAllowInScreen())
                continue;
            binding.setPressed(false);
            if (binding.getActionMode() == ActionMode.PRESS) {
                binding.setDirty(true);
            }
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
        return modifiers + (code << 4);
    }

    public void handleMouse(Window window, int button, int action, int modifiers) {
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

    public void handleKey(Window window, int key, int scancode, int action, int modifiers) {
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
        boolean displayingScreen = engineClient.getRenderContext().getGuiManager().isDisplayingScreen();
        if (displayingScreen) {
            releaseAllPressedKeys(false);
        }

        for (KeyBinding keyBinding : indexToBinding.values()) {
            if (displayingScreen && !keyBinding.isAllowInScreen()) continue;
            if (keyBinding.isDirty()) {
                // state change
                keyBinding.setDirty(false);
                if (keyBinding.getActionMode() == ActionMode.PRESS) {
                    keyBinding.setActive(keyBinding.isPressed());
                } else {
                    if (keyBinding.isPressed()) {
                        keyBinding.setActive(!keyBinding.isActive());
                    }
                }
                if (keyBinding.isActive()) {
                    keyBinding.onKeyStart(engineClient);
                } else {
                    keyBinding.onKeyEnd(engineClient);
                }
            } else if (keyBinding.isActive()) {
                // keep key
                keyBinding.onKeyKeep(engineClient);
            }
        }
    }

    private void releaseAllPressedKeys(boolean releaseAll) {
        for (KeyBinding keyBinding : indexToBinding.values()) {
            if (!releaseAll && keyBinding.isAllowInScreen()) continue;
            if (keyBinding.isDirty()) {
                // state change
                keyBinding.setDirty(false);
                if (keyBinding.getActionMode() == ActionMode.PRESS) {
                    keyBinding.setActive(keyBinding.isPressed());
                } else {
                    if (keyBinding.isPressed()) {
                        keyBinding.setActive(!keyBinding.isActive());
                    }
                }
                if (keyBinding.isActive()) {
                    keyBinding.setPressed(false);
                    keyBinding.setActive(false);
                } else {
                    keyBinding.onKeyEnd(engineClient);
                }
            } else if (keyBinding.isActive()) {
                keyBinding.setPressed(false);
                keyBinding.setActive(false);
                keyBinding.onKeyEnd(engineClient);
            }
        }
    }

    @Override
    public List<String> getRegisteredKeyBindings() {
        return ImmutableList.copyOf(registry.getKeys());
    }

    @Override
    public Key getBoundKeyFor(String target) {
        return registry.getValue(target).getKey();
    }

    @Override
    public void setBoundKeyFor(String target, Key key) {
        registry.getValue(target).setKey(key);
    }

    @Override
    public void setBoundKeyToDefault(String target) {
        KeyBinding binding = registry.getValue(target);
        binding.setKey(binding.getDefaultKey());
    }

    @Override
    public void saveConfig() {
        reload();
        // TODO: save config file to disk
    }

    public void loadConfig() {
        // TODO: load config file from disk
    }

}
