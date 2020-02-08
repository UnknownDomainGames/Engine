package engine.graphics.management;

import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.util.GPUInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface RenderManager {

    Thread getRenderingThread();

    boolean isRenderingThread();

    GPUInfo getGPUInfo();

    WindowHelper getWindowHelper();

    Window getPrimaryWindow();

    ResourceFactory getResourceFactory();

    void attachHandler(RenderHandler handler);

    Future<Void> submitTask(Runnable runnable);

    <V> Future<V> submitTask(Callable<V> callable);

    void render(float tpf);

    void init();

    void dispose();
}
