package unknowndomain.engine.event;

import unknowndomain.engine.Engine;

/**
 * Events related to the Engine
 */
public class EngineEvent implements Event {
    /** The Engine */
    private Engine engine;

    protected EngineEvent(Engine e) {
        engine = e;
    }

    public Engine getEngine() {
        return engine;
    }

    /* Fired when the Engine starts its construction */
    public static class ConstructionStart extends EngineEvent {
        public ConstructionStart(Engine e) {
            super(e);
        }
    }
}
