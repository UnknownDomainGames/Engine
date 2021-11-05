package engine.graphics.backend;

import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.util.GPUInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface GraphicsBackend {

    String getName();

    Thread getRenderingThread();

    boolean isRenderingThread();

    GPUInfo getGPUInfo();

    WindowHelper getWindowHelper();

    Window getPrimaryWindow();

    ResourceFactory getResourceFactory();

    RenderGraph loadRenderGraph(RenderGraphInfo renderGraph);

    void removeRenderGraph(RenderGraph renderGraph);

    Future<?> runLater(Runnable runnable);

    <V> Future<V> runLater(Callable<V> callable);

    void render(float tpf);

    void init();

    void dispose();
}
