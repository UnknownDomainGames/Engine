package engine.graphics.graph;

import engine.graphics.texture.FrameBuffer;

public interface RenderPass {
    RenderPassInfo getInfo();

    RenderTask getRenderTask();

    FrameBuffer getFrameBuffer();
}
