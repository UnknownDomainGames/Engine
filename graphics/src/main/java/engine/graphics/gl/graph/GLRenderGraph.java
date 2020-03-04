package engine.graphics.gl.graph;

import engine.graphics.display.Window;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.graph.Frame;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.graph.RenderTask;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;

import java.util.HashMap;
import java.util.Map;

import static engine.graphics.gl.texture.GLFrameBuffer.getDefaultFrameBuffer;
import static org.apache.commons.lang3.Validate.notNull;

public final class GLRenderGraph implements RenderGraph {
    private final RenderGraphInfo info;

    private final GLRenderTask mainTask;
    private final Map<String, GLRenderTask> tasks = new HashMap<>();

    private int frameNumber = 0;
    private long lastFrameStartTime;

    private int width;
    private int height;
    private boolean resized;
    private Window window;

    public GLRenderGraph(RenderGraphInfo info) {
        this.info = info;
        info.getTasks().forEach(task -> tasks.put(task.getName(), new GLRenderTask(task, this)));
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

    public void draw(float tickLastFrame) {
        if (window != null && window.isResized()) {
            setSize(window.getWidth(), window.getHeight());
        }

        frameNumber++;
        long frameStartTime = System.nanoTime();
        float timeLastFrame = (frameStartTime - lastFrameStartTime) / 1e9f;
        mainTask.draw(new Frame(frameNumber, System.currentTimeMillis(), timeLastFrame, tickLastFrame, width, height, resized));
        lastFrameStartTime = frameStartTime;
        resized = false;

        swapBuffers();
    }

    private void swapBuffers() {
        if (window == null) return;
        FrameBuffer result = getMainTask().getFinalPass().getFrameBuffer();
        GLFrameBuffer screen = getDefaultFrameBuffer();
        screen.resize(result.getWidth(), result.getHeight());
        screen.copyFrom(result, true, false, false, FilterMode.NEAREST);
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
