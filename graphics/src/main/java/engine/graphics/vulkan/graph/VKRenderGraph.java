package engine.graphics.vulkan.graph;

import engine.graphics.display.Window;
import engine.graphics.graph.*;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.vulkan.CommandBuffer;

import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

public class VKRenderGraph implements RenderGraph {
    private final RenderGraphInfo info;

    private final VKRenderTask mainTask;
    private final Map<String, VKRenderTask> tasks;

    private int frameNumber = 0;
    private long lastFrameStartTime;

    private int width;
    private int height;
    private boolean resized;
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.resized = true;
    }

    @Override
    public FrameBuffer getOutputFrameBuffer() {
        return null;
    }

    public void draw(float timeToLastUpdate, CommandBuffer mainCmdBuf) {
        window.prepareDraw();
        if (window != null && window.isResized()) {
            setSize(window.getWidth(), window.getHeight());
        }

        frameNumber++;
        long frameStartTime = System.nanoTime();
        long currentTimeMillis = System.currentTimeMillis();
        float timeLastFrame = (frameStartTime - lastFrameStartTime) / 1e9f;
        Frame frame = new Frame(frameNumber, currentTimeMillis, timeLastFrame, timeToLastUpdate, width, height, resized);
        mainTask.draw(frame, mainCmdBuf);
        lastFrameStartTime = frameStartTime;
        resized = false;
    }

    public void dispose() {
        tasks.values().forEach(VKRenderTask::dispose);
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void bindWindow(Window window) {
        this.window = notNull(window);
    }

    @Override
    public void unbindWindow() {
        this.window = null;
    }
}
