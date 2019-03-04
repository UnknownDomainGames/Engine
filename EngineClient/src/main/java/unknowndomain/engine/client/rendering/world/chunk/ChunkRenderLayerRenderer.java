package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;

public interface ChunkRenderLayerRenderer {

    void preRender();

    void render(GLBuffer buffer, int offset, int count);

    void postRender();
}
