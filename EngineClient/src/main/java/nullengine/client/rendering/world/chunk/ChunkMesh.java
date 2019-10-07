package nullengine.client.rendering.world.chunk;

import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.util.VertexBufferObject;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.disposer.Disposable;
import nullengine.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMesh implements Disposable {

    private AtomicInteger dirtyCount = new AtomicInteger(0);

    private VertexBufferObject chunkSolidVbo;

    private final Chunk chunk;

    private boolean disposed = false;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
    }

    public void upload(GLBuffer buffer) {
        if (chunkSolidVbo == null) {
            chunkSolidVbo = new VertexBufferObject(VertexBufferObject.Usage.DYNAMIC_DRAW);
        }
        chunkSolidVbo.uploadData(buffer.getBackingBuffer(), buffer.getVertexCount());
    }

    public void render() {
        if (chunk.isAirChunk()) {
            return;
        }

        if (chunkSolidVbo == null) {
            return;
        }

        chunkSolidVbo.bind();
        ShaderProgram.pointVertexAttribute(0, 3, 36 + 12, 0);
        ShaderProgram.enableVertexAttrib(0);
        ShaderProgram.pointVertexAttribute(1, 4, 36 + 12, 12);
        ShaderProgram.enableVertexAttrib(1);
        ShaderProgram.pointVertexAttribute(2, 2, 36 + 12, 28);
        ShaderProgram.enableVertexAttrib(2);
        ShaderProgram.pointVertexAttribute(3, 3, 36 + 12, 28 + 8);
        ShaderProgram.enableVertexAttrib(3);
        chunkSolidVbo.drawArrays(GL11.GL_TRIANGLES);
        chunkSolidVbo.unbind();
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

        if (chunkSolidVbo != null) {
            chunkSolidVbo.dispose();
        }
    }

    public boolean isDisposed() {
        return disposed;
    }
}
