package nullengine.client.rendering.management;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.scene.ViewPort;
import nullengine.client.rendering.util.GPUInfo;

public interface RenderManager {

    Thread getRenderingThread();

    boolean isRenderingThread();

    ResourceFactory getResourceFactory();

    ViewPort getPrimaryViewPort();

    void setPrimaryViewPort(ViewPort viewPort);

    GPUInfo getGPUInfo();

    Window getPrimaryWindow();

    boolean isAutoSwapBuffers();

    void setAutoSwapBuffers(boolean autoSwapBuffers);

    void render(float tpf);

    void init();

    void dispose();
}
