package nullengine.server;

import nullengine.Engine;

public interface EngineServer extends Engine {

    Thread getServerThread();

    boolean isServerThread();
}
