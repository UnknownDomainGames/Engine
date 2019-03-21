package unknowndomain.engine.event.engine;

import unknowndomain.engine.Engine;
import unknowndomain.engine.event.Event;

/**
 * Events related to the Engine
 */
public class EngineEvent implements Event {
    /**
     * The Engine
     */
    private Engine engine;

    protected EngineEvent(Engine e) {
        engine = e;
    }

    public Engine getEngine() {
        return engine;
    }

    /* Fired when the Engine finishes initializing and is ready to start games */
    public static class InitializationComplete extends EngineEvent {
        public InitializationComplete(Engine e) {
            super(e);
        }
    }
}
