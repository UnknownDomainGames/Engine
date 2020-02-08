package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.gui.GUIManager;
import nullengine.client.hud.HUDManager;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering3d.viewport.Viewport;

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
