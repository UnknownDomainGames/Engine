package unknowndomain.engine.client.rendering.world.chunk;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.util.VertexBufferObject;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.Disposable;
import unknowndomain.engine.world.chunk.Chunk;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMesh implements Disposable {

    private AtomicInteger changeCount = new AtomicInteger(0);

    private VertexBufferObject chunkSolidVbo;

    private final ChunkPos chunkPos;
    private final Chunk chunk;

    public ChunkMesh(ChunkPos chunkPos, Chunk chunk) {
        this.chunkPos = chunkPos;
        this.chunk = chunk;
    }

    public void upload(ByteBuffer buffer, int vertexCount) {
        if (chunkSolidVbo == null) {
            chunkSolidVbo = new VertexBufferObject();
        }
        chunkSolidVbo.uploadData(buffer, vertexCount);
    }

    public void render() {
        if (chunk.isAirChunk()) {
            return;
        }

        if (chunkSolidVbo == null) {
            return;
        }

        chunkSolidVbo.bind();
        Shader.pointVertexAttribute(0, 3, 36, 0);
        Shader.enableVertexAttrib(0);
        Shader.pointVertexAttribute(1, 4, 36, 12);
        Shader.enableVertexAttrib(1);
        Shader.pointVertexAttribute(2, 2, 36, 28);
        Shader.enableVertexAttrib(2);
        chunkSolidVbo.drawArrays(GL11.GL_TRIANGLES);
        chunkSolidVbo.unbind();
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void markDirty() {
        changeCount.getAndIncrement();
    }

    public boolean isDirty() {
        return changeCount.get() != 0;
    }

    public void startBake() {
        changeCount.set(0);
    }

    @Override
    public void dispose() {
        chunkSolidVbo.dispose();
    }
}
