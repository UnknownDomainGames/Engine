package nullengine.client.rendering.world.chunk.layer;

import nullengine.client.rendering.gl.GLBuffer;

public interface ChunkLayerRenderer {

    void preRender();

    void render(GLBuffer buffer, int offset, int count);

    void postRender();
}
