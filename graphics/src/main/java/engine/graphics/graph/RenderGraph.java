package engine.graphics.graph;

import engine.graphics.display.Window;
import engine.graphics.texture.FrameBuffer;

import java.util.Map;
import java.util.function.Consumer;

public interface RenderGraph {

    RenderGraphInfo getInfo();

    RenderTask getMainTask();

    RenderTask getTask(String name);

    void dispatchTask(String name, Frame frame, Map<String, Object> args, Consumer<RenderTask> callback);

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    FrameBuffer getOutputFrameBuffer();

    Window getWindow();

    void bindWindow(Window window);

    void unbindWindow();
}
