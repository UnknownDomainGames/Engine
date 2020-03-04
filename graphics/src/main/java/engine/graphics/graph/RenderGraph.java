package engine.graphics.graph;

import engine.graphics.display.Window;

public interface RenderGraph {

    RenderGraphInfo getInfo();

    RenderTask getMainTask();

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Window getWindow();

    void bindWindow(Window window);

    void unbindWindow();
}
