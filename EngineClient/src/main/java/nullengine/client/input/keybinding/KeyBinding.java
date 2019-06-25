package nullengine.client.input.keybinding;

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
    /**
     * Handles Single press of the key
     */
    private final Consumer<GameClient> keyStartHandler;
    /**
     * Handles keeping of the key
     */
    private Optional<BiConsumer<GameClient, Integer>> keepHandler;
    /**
     * Handles release of the key
     */
    private Optional<BiConsumer<GameClient, Integer>> endHandler;
    /**
     * Time Elapsed while holding the key
     */
    private int timeElapsed;

    private KeyBinding(Key defaultKey, @Nonnull Consumer<GameClient> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        Validate.notNull(keyStartHandler);
        this.keyStartHandler = keyStartHandler;
        this.key = defaultKey;
        this.defaultKey = defaultKey;
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
    public static KeyBinding create(String target, Key defaultKey, @Nonnull Consumer<GameClient> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        return new KeyBinding(Key.getKeySafe(defaultKey), keyStartHandler, actionMode != null ? actionMode : ActionMode.PRESS, keyMods == null || keyMods.length == 0 ? KeyModifier.EMPTY : keyMods)
                .registerName(target);
    }

    /**
     * Bind a keep action to this KeyBinding. This method is chainable.
     *
     * @param keepHandler Keep Action
     * @return This KeyBinding
     */
    public KeyBinding keepAction(BiConsumer<GameClient, Integer> keepHandler) {
        this.keepHandler = Optional.ofNullable(keepHandler);
        return this;
    }

    /**
     * Bind an end action to this KeyBinding. This method is chainable.
     *
     * @param endHandler End Action
     * @return This KeyBinding
     */
    public KeyBinding endAction(BiConsumer<GameClient, Integer> endHandler) {
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

    public void onKeyStart(GameClient context) {
        Validate.notNull(keyStartHandler);
        keyStartHandler.accept(context);
    }

    public void onKeyKeep(GameClient context) {
        onKeepable(context, keepHandler);
        timeElapsed++;
    }

    public void onKeyEnd(GameClient context) {
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
    private void onKeepable(GameClient context, Optional<BiConsumer<GameClient, Integer>> handler) {
        handler.ifPresent((handle) -> handle.accept(context, timeElapsed));
    }

    public void rebind(Key key) {
        this.key = Key.getKeySafe(key);
    }

    public Key getDefaultKey() {
        return this.defaultKey;
    }

}
