package nullengine.client.input.keybinding;

import nullengine.client.EngineClient;
import nullengine.client.game.GameClient;
import nullengine.registry.RegistryEntry;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles the state of a single KeyBinding
 *
 * @author Mouse0w0 and iTNTPiston
 */
public class KeyBinding extends RegistryEntry.Impl<KeyBinding> {
    private Key key;
    private final Key defaultKey;
    private final KeyModifier[] mods;
    private final ActionMode actionMode;
    private boolean active = false;
    private boolean pressed = false;
    private boolean dirty = false;
    private final boolean allowInScreen;
    /**
     * Handles Single press of the key
     */
    private final Consumer<EngineClient> keyStartHandler;
    /**
     * Handles keeping of the key
     */
    private Optional<BiConsumer<EngineClient, Integer>> keepHandler;
    /**
     * Handles release of the key
     */
    private Optional<BiConsumer<EngineClient, Integer>> endHandler;
    /**
     * Time Elapsed while holding the key
     */
    private int timeElapsed;

    private KeyBinding(Key defaultKey, boolean allowInScreen, @Nonnull Consumer<EngineClient> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        Validate.notNull(keyStartHandler);
        this.keyStartHandler = keyStartHandler;
        this.key = defaultKey;
        this.defaultKey = defaultKey;
        this.allowInScreen = allowInScreen;
        this.actionMode = actionMode;
        this.mods = keyMods;
        keepHandler = endHandler = Optional.empty();
    }

    /**
     * Create a KeyBinding with a specific key, its press action and its action mode
     * and modifiers
     *
     * @param defaultKey
     * @param actionMode
     * @param keyMods
     * @return
     */
    public static KeyBinding create(String target, Key defaultKey, @Nonnull Consumer<EngineClient> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        return create(target, false, defaultKey, keyStartHandler, actionMode, keyMods);
    }

    /**
     * Create a KeyBinding with a specific key, its press action and its action mode
     * and modifiers
     *
     *
     * @param allowInScreen true if this keybinding should still handling events despite showing gui screen
     * @param defaultKey
     * @param actionMode
     * @param keyMods
     * @return
     */
    public static KeyBinding create(String target, boolean allowInScreen, Key defaultKey, @Nonnull Consumer<EngineClient> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        return new KeyBinding(Key.getKeySafe(defaultKey), allowInScreen, keyStartHandler, actionMode != null ? actionMode : ActionMode.PRESS, keyMods == null || keyMods.length == 0 ? KeyModifier.EMPTY : keyMods)
                .name(target);
    }

    /**
     * Bind a keep action to this KeyBinding. This method is chainable.
     *
     * @param keepHandler Keep Action
     * @return This KeyBinding
     */
    public KeyBinding keepAction(BiConsumer<EngineClient, Integer> keepHandler) {
        this.keepHandler = Optional.ofNullable(keepHandler);
        return this;
    }

    /**
     * Bind an end action to this KeyBinding. This method is chainable.
     *
     * @param endHandler End Action
     * @return This KeyBinding
     */
    public KeyBinding endAction(BiConsumer<EngineClient, Integer> endHandler) {
        this.endHandler = Optional.ofNullable(endHandler);
        return this;
    }

    /**
     * Get the registered name of the key binding
     *
     * @Deprecated Use {@link RegistryEntry.getUniqueName()} directly
     */
    @Deprecated
    public String getTarget() {
        return this.getUniqueName();
    }

    public Key getKey() {
        return key;
    }

    public boolean isAllowInScreen() {
        return allowInScreen;
    }

    public KeyModifier[] getModifier() {
        return mods;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void onKeyStart(EngineClient context) {
        Validate.notNull(keyStartHandler);
        keyStartHandler.accept(context);
    }

    public void onKeyKeep(EngineClient context) {
        onKeepable(context, keepHandler);
        timeElapsed++;
    }

    public void onKeyEnd(EngineClient context) {
        onKeepable(context, endHandler);
        timeElapsed = 0;
    }

    /**
     * Handles keepable key action (keep or end)
     *
     * @param context
     * @param handler
     * @see #onKeyKeep(GameClient)
     */
    private void onKeepable(EngineClient context, Optional<BiConsumer<EngineClient, Integer>> handler) {
        handler.ifPresent((handle) -> handle.accept(context, timeElapsed));
    }

    public void rebind(Key key) {
        this.key = Key.getKeySafe(key);
    }

    public Key getDefaultKey() {
        return this.defaultKey;
    }

}
