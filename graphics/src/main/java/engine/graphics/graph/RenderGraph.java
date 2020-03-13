package engine.graphics.graph;

import engine.graphics.display.Window;

import java.util.Map;

public interface RenderGraph {

    RenderGraphInfo getInfo();

    RenderTask getMainTask();

    RenderTask getTask(String name);

    RenderTask dispatchTask(String name, Frame frame, Map<String, Object> args);

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Window getWindow();

    void bindWindow(Window window);

    void unbindWindow();
}
