package engine.graphics.graph;

import engine.graphics.texture.Texture;

public interface RenderTask {
    RenderTaskInfo getInfo();

    RenderGraph getRenderGraph();

    RenderPass getFinalPass();

    Texture getRenderBuffer(String name);
}
