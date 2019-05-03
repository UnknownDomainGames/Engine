package unknowndomain.engine.event.mod;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.mod.ModContainer;

public class ModLifecycleEvent implements Event {

    private final ModContainer modContainer;

    public ModLifecycleEvent(ModContainer modContainer) {
        this.modContainer = modContainer;
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public static final class Construction extends ModLifecycleEvent {
        public Construction(ModContainer modContainer) {
            super(modContainer);
        }
    }

    public static final class Initialization extends ModLifecycleEvent {
        public Initialization(ModContainer modContainer) {
            super(modContainer);
        }
    }

    public static final class Ready extends ModLifecycleEvent {
        public Ready(ModContainer modContainer) {
            super(modContainer);
        }
    }

    public static final class Unload extends ModLifecycleEvent {
        public Unload(ModContainer modContainer) {
            super(modContainer);
        }
    }
}
