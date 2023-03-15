package engine.client.input.keybinding;

import engine.client.EngineClient;
import engine.input.Modifiers;
import engine.registry.Registrable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles the state of a single KeyBinding
 *
 * @author Mouse0w0 and iTNTPiston
 */
public final class KeyBinding extends Registrable.Impl<KeyBinding> {

    private final Key defaultKey;
    private final Modifiers defaultModifier;
    private final ActionMode defaultActionMode;
    private final boolean allowChangeActionMode;
    private final boolean allowInScreen;

    /**
     * Handles Single press of the key
     */
    private final Consumer<EngineClient> startHandler;
    /**
     * Handles keeping of the key
     */
    private final BiConsumer<EngineClient, Integer> keepHandler;
    /**
     * Handles release of the key
     */
    private final BiConsumer<EngineClient, Integer> endHandler;

    private Key key;
    private Modifiers modifier;
    private ActionMode actionMode;

    private boolean active = false;
    private boolean pressed = false;
    private boolean dirty = false;

    /**
     * Time Elapsed while holding the key
     */
    private int timeElapsed;

    private KeyBinding(Builder builder) {
        name(builder.name);
        this.defaultKey = builder.defaultKey;
        this.defaultModifier = builder.defaultModifier;
        this.defaultActionMode = builder.defaultActionMode;
        this.key = builder.defaultKey;
        this.modifier = builder.defaultModifier;
        this.actionMode = builder.defaultActionMode;
        this.allowChangeActionMode = builder.allowChangeActionMode;
        this.allowInScreen = builder.allowInScreen;
        this.startHandler = builder.startHandler;
        this.keepHandler = builder.keepHandler;
        this.endHandler = builder.endHandler;
    }

    public Key getDefaultKey() {
        return this.defaultKey;
    }

    public Modifiers getDefaultModifier() {
        return defaultModifier;
    }

    public ActionMode getDefaultActionMode() {
        return defaultActionMode;
    }

    public boolean isAllowChangeActionMode() {
        return allowChangeActionMode;
    }

    public boolean isAllowInScreen() {
        return allowInScreen;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key == null ? Key.KEY_UNKNOWN : key;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    public void setModifier(Modifiers modifier) {
        this.modifier = modifier == null ? Modifiers.of() : modifier;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        if (!isAllowChangeActionMode())
            throw new UnsupportedOperationException("Key binding isn't allow change action mode");
        this.actionMode = actionMode;
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
        if (startHandler != null) {
            startHandler.accept(context);
        }
    }

    public void onKeyKeep(EngineClient context) {
        if (keepHandler != null) {
            keepHandler.accept(context, timeElapsed);
        }
        timeElapsed++;
    }

    public void onKeyEnd(EngineClient context) {
        if (endHandler != null) {
            endHandler.accept(context, timeElapsed);
        }
        timeElapsed = 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Key defaultKey = Key.KEY_UNKNOWN;
        private Modifiers defaultModifier = Modifiers.of();
        private ActionMode defaultActionMode = ActionMode.PRESS;
        private boolean allowChangeActionMode = false;
        private boolean allowInScreen = false;
        private Consumer<EngineClient> startHandler;
        private BiConsumer<EngineClient, Integer> keepHandler;
        private BiConsumer<EngineClient, Integer> endHandler;
        private String name;

        public Builder key(Key defaultKey) {
            this.defaultKey = defaultKey == null ? Key.KEY_UNKNOWN : defaultKey;
            return this;
        }

        public Builder modifier(Modifiers defaultModifier) {
            this.defaultModifier = defaultModifier == null ? Modifiers.of() : defaultModifier;
            return this;
        }

        public Builder actionMode(ActionMode defaultActionMode) {
            this.defaultActionMode = defaultActionMode == null ? ActionMode.PRESS : defaultActionMode;
            return this;
        }

        public Builder allowChangeActionMode(boolean allowChangeActionMode) {
            this.allowChangeActionMode = allowChangeActionMode;
            return this;
        }

        public Builder allowInScreen(boolean allowInScreen) {
            this.allowInScreen = allowInScreen;
            return this;
        }

        public Builder startHandler(Consumer<EngineClient> startHandler) {
            this.startHandler = startHandler;
            return this;
        }

        public Builder keepHandler(BiConsumer<EngineClient, Integer> keepHandler) {
            this.keepHandler = keepHandler;
            return this;
        }

        public Builder endHandler(BiConsumer<EngineClient, Integer> endHandler) {
            this.endHandler = endHandler;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public KeyBinding build() {
            return new KeyBinding(this);
        }
    }
}
