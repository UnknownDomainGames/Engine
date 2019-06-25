package nullengine.client.rendering.world.chunk.layer;

import nullengine.client.rendering.util.buffer.GLBuffer;

public interface ChunkLayerRenderer {

    void preRender();

    void render(GLBuffer buffer, int offset, int count);

    void postRender();
}
