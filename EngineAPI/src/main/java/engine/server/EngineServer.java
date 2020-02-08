package engine.server;

import engine.Engine;

public interface EngineServer extends Engine {

    Thread getServerThread();

    boolean isServerThread();
}
