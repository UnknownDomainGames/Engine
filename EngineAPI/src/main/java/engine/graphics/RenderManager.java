package engine.graphics;

import engine.client.EngineClient;
import engine.gui.GUIManager;
import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.graphics.texture.TextureManager;
import engine.graphics.viewport.Viewport;

import java.util.function.Supplier;

public interface RenderManager {

    EngineClient getEngine();

    Thread getRenderThread();

    boolean isRenderThread();

    Window getWindow();

    Viewport getViewport();

    TextureManager getTextureManager();

    GUIManager getGUIManager();

    HUDManager getHUDManager();

    int getFPS();

    static RenderManager instance() {
        return RenderManager.Internal.instance.get();
    }

    class Internal {
        private static Supplier<RenderManager> instance = () -> {
            throw new IllegalStateException("TextureManager is uninitialized");
        };

        public static void setInstance(RenderManager instance) {
            RenderManager.Internal.instance = () -> instance;
        }
    }
}
