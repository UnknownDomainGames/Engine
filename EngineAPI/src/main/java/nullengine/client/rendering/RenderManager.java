package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.gui.GuiManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.display.DisplayInfo;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering.util.GLInfo;
import nullengine.client.rendering.util.GPUMemoryInfo;
import nullengine.component.GameObject;
import nullengine.exception.UninitializationException;
import org.joml.FrustumIntersection;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface RenderManager extends GameObject {

    EngineClient getEngine();

    Thread getRenderThread();

    boolean isRenderThread();

    Window getWindow();

    @Nonnull
    Camera getCamera();

    void setCamera(@Nonnull Camera camera);

    FrustumIntersection getFrustumIntersection();

    TextureManager getTextureManager();

    GuiManager getGuiManager();

    RenderScheduler getScheduler();

    DisplayInfo getDisplayInfo();

    GLInfo getGLInfo();

    GPUMemoryInfo getGPUMemoryInfo();

    int getFPS();

    static RenderManager instance() {
        return RenderManager.Internal.instance.get();
    }

    class Internal {
        private static Supplier<RenderManager> instance = UninitializationException.supplier("TextureManager is uninitialized");

        public static void setInstance(RenderManager instance) {
            RenderManager.Internal.instance = () -> instance;
        }
    }
}
