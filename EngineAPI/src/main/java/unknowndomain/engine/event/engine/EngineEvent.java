package unknowndomain.engine.event.engine;

import unknowndomain.engine.Engine;
import unknowndomain.engine.event.Event;

public class EngineEvent implements Event {

    private Engine engine;

    private EngineEvent(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public static class Ready extends EngineEvent {
        public Ready(Engine engine) {
            super(engine);
        }
    }

    public static class MarkedTermination extends EngineEvent {
        public MarkedTermination(Engine engine) {
            super(engine);
        }
    }

    public static class PreTermination extends EngineEvent {
        public PreTermination(Engine engine) {
            super(engine);
        }
    }
}
