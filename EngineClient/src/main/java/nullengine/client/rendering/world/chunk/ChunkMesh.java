package nullengine.client.rendering.world.chunk;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.SingleBufferVAO;
import nullengine.client.rendering.gl.vertex.GLVertexFormats;
import nullengine.util.Disposable;
import nullengine.world.chunk.Chunk;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMesh implements Disposable {

    private AtomicInteger dirtyCount = new AtomicInteger(0);

    private SingleBufferVAO chunkSolidVAO;

    private final Chunk chunk;

    private boolean disposed = false;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
    }

    public void upload(GLBuffer buffer) {
        if (chunkSolidVAO == null) {
            chunkSolidVAO = new SingleBufferVAO();
            chunkSolidVAO.setVertexFormat(GLVertexFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
        }
        chunkSolidVAO.uploadData(buffer);
    }

    public void render() {
        if (chunk.isAirChunk()) {
            return;
        }

        if (chunkSolidVAO == null) {
            return;
        }

        chunkSolidVAO.draw();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void markDirty() {
        dirtyCount.getAndIncrement();
    }

    public boolean isDirty() {
        return dirtyCount.get() != 0;
    }

    public void clearDirty() {
        dirtyCount.set(0);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;

        if (chunkSolidVAO != null) {
            chunkSolidVAO.dispose();
        }
    }

    public boolean isDisposed() {
        return disposed;
    }
}
