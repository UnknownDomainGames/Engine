package nullengine.client.rendering.world.chunk;

import nullengine.client.rendering.gl.GLSingleBufferMesh;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.util.Disposable;
import nullengine.world.chunk.Chunk;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMesh implements Disposable {

    private AtomicInteger dirtyCount = new AtomicInteger(0);

    private GLSingleBufferMesh chunkSolidMesh;

    private final Chunk chunk;

    private boolean disposed = false;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
    }

    public void upload(VertexDataBuf buffer) {
        if (chunkSolidMesh == null) {
            chunkSolidMesh = GLSingleBufferMesh.builder().setDynamic().build();
        }
        chunkSolidMesh.uploadData(buffer);
    }

    public void render() {
        if (chunk.isAirChunk()) {
            return;
        }

        if (chunkSolidMesh == null) {
            return;
        }

        chunkSolidMesh.draw();
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

        if (chunkSolidMesh != null) {
            chunkSolidMesh.dispose();
        }
    }

    public boolean isDisposed() {
        return disposed;
    }
}
