package unknowndomain.engine.server;

import unknowndomain.engine.api.event.EventBusUniversal;

public class EngineServer {
    public static final EngineServer INSTANCE = new EngineServer();

    public final EventBusUniversal registryEventBus = new EventBusUniversal();

}
