package engine.graphics.graph;

import engine.graphics.display.Window;
import engine.graphics.texture.FrameBuffer;

public interface RenderGraph {

    RenderGraphInfo getInfo();

    RenderTask getMainTask();

    RenderTask getTask(String name);

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    FrameBuffer getOutputFrameBuffer();

    Window getWindow();

    void bindWindow(Window window);

    void unbindWindow();
}
