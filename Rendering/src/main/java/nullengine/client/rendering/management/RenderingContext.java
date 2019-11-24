package nullengine.client.rendering.management;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.scene.Scene;
import nullengine.client.rendering.util.GPUInfo;

public interface RenderingContext {

    Thread getRenderingThread();

    boolean isRenderingThread();

    GPUInfo getGPUInfo();

    Window getPrimaryWindow();

    Scene getScene();

    void setScene(Scene scene);

    void render(float partial);

    void init();

    void dispose();
}
