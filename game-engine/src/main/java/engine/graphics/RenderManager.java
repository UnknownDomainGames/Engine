package engine.graphics;

import engine.client.EngineClient;
import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.graphics.viewport.PerspectiveViewport;
import engine.gui.GUIManager;

import java.util.function.Supplier;

public interface RenderManager {

    EngineClient getEngine();

    Thread getRenderThread();

    boolean isRenderThread();

    Window getWindow();

    PerspectiveViewport getViewport();

    GUIManager getGUIManager();

    HUDManager getHUDManager();

    int getFPS();

    static RenderManager instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<RenderManager> instance = () -> {
            throw new IllegalStateException("RenderManager is uninitialized");
        };

        public static void setInstance(RenderManager instance) {
            Internal.instance = () -> instance;
        }
    }
}
