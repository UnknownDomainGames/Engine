package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.util.Disposable;
import unknowndomain.engine.world.chunk.Chunk;

public class BakedChunkRender implements Disposable {

    private final Chunk chunk;

    public BakedChunkRender(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void dispose() {

    }
}
