package engine.graphics;

import engine.client.EngineClient;
import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.graphics.viewport.PerspectiveViewport;
import engine.gui.GUIManager;

import java.util.function.Supplier;

public interface GraphicsManager {

    EngineClient getEngine();

    Thread getRenderThread();

    boolean isRenderThread();

    Window getWindow();

    Scene3D getScene();

    PerspectiveViewport getViewport();

    GUIManager getGUIManager();

    HUDManager getHUDManager();

    int getFPS();

    static GraphicsManager instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<GraphicsManager> instance = () -> {
            throw new IllegalStateException("GraphicsManager is uninitialized");
        };

        static void setInstance(GraphicsManager instance) {
            Internal.instance = () -> instance;
        }
    }
}
