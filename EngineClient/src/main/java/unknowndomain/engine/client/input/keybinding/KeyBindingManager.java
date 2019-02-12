package unknowndomain.engine.client.input.keybinding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.Platform;
import unknowndomain.engine.Tickable;
import unknowndomain.engine.client.game.ClientContext;
import unknowndomain.engine.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the registration of KeyBinding and also handles key inputs (and mouse
 * inputs)
 */
public class KeyBindingManager implements Tickable, KeyBindingConfig {

    /**
     * Mappes the key binding index to the KeyBinding objects.
     */
    private final Multimap<Integer, KeyBinding> indexToBinding = HashMultimap.create();
    /**
     * KeyBinding Registry
     */
    private final Registry<KeyBinding> registry;
    private ClientContext gameContext;
    /**
     * @Deprecated Not used.
     */
    @Deprecated
    private final Set<Key> pressedKey = new HashSet<>();

    public KeyBindingManager(Registry<KeyBinding> keyBindingRegistry) {
        registry = keyBindingRegistry;
    }

    /**
     * Set the GameContext when a game starts
     *
     * @param context
     */
    public void setGameContext(ClientContext context) {
        gameContext = context;
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
            byte mods = KeyModifier.getCode(keybinding.getModifier());
            indexToBinding.put(getIndex(code, mods), keybinding);
        }
    }

    protected void handlePress(int code, int modifiers) {
        // TODO: Remove it, hard code.
        if (Platform.getEngineClient().getGuiManager().isDisplayingScreen()) {
            return;
        }

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
        // TODO: Remove it, hard code.
        if (Platform.getEngineClient().getGuiManager().isDisplayingScreen()) {
            return;
        }

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
        return modifiers + (code << 4);
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

    @Override
    public List<String> getRegisteredKeyBindings() {
        return ImmutableList.copyOf(registry.getKeys());
    }

    @Override
    public Key getBindedKeyFor(String target) {
        return registry.getValue(target).getKey();
    }

    @Override
    public void setBindedKeyFor(String target, Key key) {
        registry.getValue(target).rebind(key);
    }

    @Override
    public void setBindedKeyToDefault(String target) {
        KeyBinding binding = registry.getValue(target);
        binding.rebind(binding.getDefaultKey());
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
