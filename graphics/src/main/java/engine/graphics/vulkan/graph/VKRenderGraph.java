package engine.graphics.vulkan.graph;

import engine.graphics.display.Window;
import engine.graphics.graph.*;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VKRenderGraph implements RenderGraph {
    private final RenderGraphInfo info;

    private final VKRenderTask mainTask;
    private final Map<String, VKRenderTask> tasks;

    private int frameNumber = 0;
    private long lastFrameStartTime;

    private Window window;

    public VKRenderGraph(RenderGraphInfo info) {
        this.info = info;
        this.tasks = info.getTasks().stream().collect(Collectors.toUnmodifiableMap(
                RenderTaskInfo::getName, taskInfo -> new VKRenderTask(taskInfo, this)));
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
    public RenderTask getTask(String name) {
        return tasks.get(name);
    }

    @Override
    public void dispatchTask(String name, Frame frame, Map<String, Object> args, Consumer<RenderTask> callback) {

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
