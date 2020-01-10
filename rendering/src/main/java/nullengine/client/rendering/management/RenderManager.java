package nullengine.client.rendering.management;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.WindowHelper;
import nullengine.client.rendering.util.GPUInfo;

public interface RenderManager {

    Thread getRenderingThread();

    boolean isRenderingThread();

    GPUInfo getGPUInfo();

    WindowHelper getWindowHelper();

    Window getPrimaryWindow();

    ResourceFactory getResourceFactory();

    boolean isAutoSwapBuffers();

    void setAutoSwapBuffers(boolean autoSwapBuffers);

    void attachPipeline(RenderPipeline pipeline);

    void render(float tpf);

    void init();

    void dispose();
}
