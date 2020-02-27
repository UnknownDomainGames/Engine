package engine.graphics.graph;

import engine.graphics.display.Window;

public interface RenderGraph {

    RenderGraphInfo getInfo();

    RenderTask getMainTask();

    Window getWindow();

    void bindWindow(Window window);

    void unbindWindow();
}
