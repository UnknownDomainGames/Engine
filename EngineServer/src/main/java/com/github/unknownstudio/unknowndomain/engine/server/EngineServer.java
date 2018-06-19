package com.github.unknownstudio.unknowndomain.engine.server;

import com.github.unknownstudio.unknowndomain.engineapi.event.EventBusUniversal;

public class EngineServer {
    public static final EngineServer INSTANCE = new EngineServer();

    public final EventBusUniversal registryEventBus = new EventBusUniversal();

}
