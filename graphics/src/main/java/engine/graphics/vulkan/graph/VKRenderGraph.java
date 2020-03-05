package engine.graphics.vulkan.graph;

import engine.graphics.display.Window;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.graph.RenderTask;

import java.util.HashMap;
import java.util.Map;

public class VKRenderGraph implements RenderGraph {
    private final RenderGraphInfo info;

    private final VKRenderTask mainTask;
    private final Map<String, VKRenderTask> tasks = new HashMap<>();

    private int frameNumber = 0;
    private long lastFrameStartTime;

    private Window window;

    public VKRenderGraph(RenderGraphInfo info) {
        this.info = info;
        info.getTasks().forEach(task -> tasks.put(task.getName(), new VKRenderTask(task, this)));
        this.mainTask = tasks.get(info.getMainTask());
    }

    @Override
    public RenderGraphInfo getInfo() {
        return info;
    }

    @Override
    public RenderTask getMainTask() {
        return mainTask;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void bindWindow(Window window) {

    }

    @Override
    public void unbindWindow() {

    }
}
