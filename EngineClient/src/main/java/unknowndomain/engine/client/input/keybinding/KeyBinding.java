package unknowndomain.engine.client.input.keybinding;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles the state of a single KeyBinding
 * 
 * @author Mouse0w0 and iTNTPiston
 */
public class KeyBinding extends RegistryEntry.Impl<KeyBinding> {
    private final Key code;
    private final KeyModifier[] mods;
    private final ActionMode actionMode;
    private boolean active = false;
    private boolean pressed = false;
    /** Handles Single press of the key */
    private final Consumer<GameContext> keyStartHandler;
    /** Handles keeping of the key */
    @Nullable
    private Optional<BiConsumer<GameContext, Integer>> keepHandler;
    /** Handles release of the key */
    @Nullable
    private Optional<BiConsumer<GameContext, Integer>> endHandler;
    /** Time Elapsed while holding the key */
    private int timeElapsed;

    private KeyBinding(Key code, @Nonnull Consumer<GameContext> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        Validate.notNull(keyStartHandler);
        this.keyStartHandler = keyStartHandler;
        this.code = code;
        this.actionMode = actionMode;
        this.mods = keyMods;
        keepHandler = endHandler = Optional.empty();
    }

    /**
     * Create a KeyBinding with a specific key, its press action and its action mode
     * and modifiers
     * 
     * @param code
     * @param actionMode
     * @param keyMods
     * @return
     */
    public static KeyBinding create(String target, Key code, @Nonnull Consumer<GameContext> keyStartHandler, ActionMode actionMode, KeyModifier... keyMods) {
        return new KeyBinding(code == null ? Key.KEY_UNKNOWN : code, keyStartHandler, actionMode != null ? actionMode : ActionMode.PRESS,
                keyMods == null || keyMods.length == 0 ? KeyModifier.EMPTY : keyMods).localName(target);
    }

    /**
     * Bind a keep action to this KeyBinding. This method is chainable.
     * 
     * @param keepHandler Keep Action
     * @return This KeyBinding
     */
    public KeyBinding keepAction(BiConsumer<GameContext, Integer> keepHandler) {
        this.keepHandler = Optional.ofNullable(keepHandler);
        return this;
    }

    /**
     * Bind an end action to this KeyBinding. This method is chainable.
     * 
     * @param keepHandler End Action
     * @return This KeyBinding
     */
    public KeyBinding endAction(BiConsumer<GameContext, Integer> endHandler) {
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

    public Key getCode() {
        return code;
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

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void onKeyStart(GameContext context) {
        Validate.notNull(keyStartHandler);
        keyStartHandler.accept(context);
    }

    public void onKeyKeep(GameContext context) {
        onKeepable(context, keepHandler);
        timeElapsed++;
    }

    public void onKeyEnd(GameContext context) {
        onKeepable(context, endHandler);
        timeElapsed = 0;
    }

    /**
     * Handles keepable key action (keep or end)
     * 
     * @see #onKeyKeep(GameContext)
     * @param gameContext
     * @param handler
     */
    private void onKeepable(GameContext context, Optional<BiConsumer<GameContext, Integer>> handler) {
        handler.ifPresent((handle) -> handle.accept(context, timeElapsed));
    }

}
