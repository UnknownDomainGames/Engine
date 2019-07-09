package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.gui.GuiManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering.util.GPUMemoryInfo;
import nullengine.component.GameObject;
import org.joml.FrustumIntersection;

import javax.annotation.Nonnull;

public interface RenderContext extends GameObject {

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

    GPUMemoryInfo getGPUMemoryInfo();

    int getFPS();
}
