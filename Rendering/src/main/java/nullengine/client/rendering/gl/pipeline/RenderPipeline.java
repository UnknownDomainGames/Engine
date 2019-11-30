package nullengine.client.rendering.gl.pipeline;

import nullengine.client.rendering.gl.texture.GLFrameBuffer;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.scene.ViewPort;

public interface RenderPipeline {

    GLFrameBuffer getFrameBuffer();

    void render(RenderManager manager, ViewPort viewPort, float partial);
}
