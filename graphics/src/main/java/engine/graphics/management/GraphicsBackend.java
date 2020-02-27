package engine.graphics.management;

import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.util.GPUInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface GraphicsBackend {

    Thread getRenderingThread();

    boolean isRenderingThread();

    void removeRenderGraph(RenderGraph renderGraph);

    GPUInfo getGPUInfo();

    WindowHelper getWindowHelper();

    Window getPrimaryWindow();

    ResourceFactory getResourceFactory();

    RenderGraph loadRenderGraph(RenderGraphInfo renderGraph);

    void attachHandler(RenderHandler handler);

    Future<Void> submitTask(Runnable runnable);

    <V> Future<V> submitTask(Callable<V> callable);

    void render(float tpf);

    void init();

    void dispose();
}
