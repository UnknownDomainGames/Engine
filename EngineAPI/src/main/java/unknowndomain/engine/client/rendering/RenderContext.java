package unknowndomain.engine.client.rendering;

import unknowndomain.engine.client.EngineClient;

public interface RenderContext {

    EngineClient getEngine();

    Thread getRenderThread();

    boolean isRenderThread();
}
