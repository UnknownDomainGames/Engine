package engine.graphics.gl.graph;

import engine.graphics.display.Window;
import engine.graphics.graph.*;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static engine.graphics.gl.texture.GLFrameBuffer.getBackBuffer;
import static org.apache.commons.lang3.Validate.notNull;

public final class GLRenderGraph implements RenderGraph {
    private final RenderGraphInfo info;

    private final GLRenderTask mainTask;
    private final Map<String, GLRenderTask> tasks;

    private int frameNumber = 0;
    private long lastFrameStartTime;

    private int width;
    private int height;
    private boolean resized;
    private Window window;

    public GLRenderGraph(RenderGraphInfo info) {
        this.info = info;
        this.tasks = info.getTasks().stream().collect(Collectors.toUnmodifiableMap(
                RenderTaskInfo::getName, taskInfo -> new GLRenderTask(taskInfo, this)));
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
        Validate.notNull(frame, "Frame cannot be null");
        GLRenderTask task = tasks.get(name);
        if (task == null) throw new IllegalArgumentException("Failed to found render task: " + name);
        task.draw(frame, args == null ? Map.of() : args);
        if (callback != null) callback.accept(task);
    }

    public Map<String, GLRenderTask> getTasks() {
        return tasks;
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
        return getMainTask().getFinalPass().getFrameBuffer();
    }

    public void draw(float timeToLastUpdate) {
        window.prepareDraw();
        if (window != null && window.isResized()) {
            setSize(window.getWidth(), window.getHeight());
        }

        frameNumber++;
        long frameStartTime = System.nanoTime();
        long currentTimeMillis = System.currentTimeMillis();
        float timeLastFrame = (frameStartTime - lastFrameStartTime) / 1e9f;
        Frame frame = new Frame(frameNumber, currentTimeMillis, timeLastFrame, timeToLastUpdate, width, height, resized);
        mainTask.draw(frame, Map.of());
        lastFrameStartTime = frameStartTime;
        resized = false;

        swapBuffers();
    }

    private void swapBuffers() {
        if (window == null) return;
        FrameBuffer result = getOutputFrameBuffer();
        getBackBuffer().copyFrom(result, true, false, false, FilterMode.NEAREST);
        window.swapBuffers();
    }

    public void dispose() {
        tasks.values().forEach(GLRenderTask::dispose);
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
