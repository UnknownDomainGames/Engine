package unknowndomain.engine.client.input.keybinding;

import java.util.List;

/**
 * Used for saving and loading key binding configurations (and possibly GUI for
 * changing controls)
 *
 */
public interface KeyBindingConfig {
    /**
     * Get the unique names of all registered keybindings
     * 
     * @return
     */
    List<String> getRegisteredKeyBindings();

    /**
     * Get the binded key for the specified target
     * 
     * @param target
     * @return
     */
    Key getBindedKeyFor(String target);

    /**
     * Set the binded key for the specified target. The change should not be applied
     * until save()
     * 
     * @param target
     * @param key
     */
    void setBindedKeyFor(String target, Key key);

    /**
     * Set the binded key to its default value
     * 
     * @param target
     */
    void setBindedKeyToDefault(String target);

    /**
     * Save the control settings
     */
    void saveConfig();
}
