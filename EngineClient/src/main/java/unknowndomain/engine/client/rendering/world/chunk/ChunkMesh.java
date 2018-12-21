package unknowndomain.engine.client.rendering.world.chunk;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.util.VertexBufferObject;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.Disposable;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public class ChunkMesh implements Disposable {

    private boolean dirty = true;

    private VertexBufferObject vbo;

    private final World world;
    private final ChunkPos chunkPos;
    private final Chunk chunk;

    public ChunkMesh(World world, ChunkPos chunkPos, Chunk chunk) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.chunk = chunk;
    }

    public void update(BufferBuilder bufferBuilder) {
        if(vbo == null) {
            vbo = new VertexBufferObject();
        }
        vbo.uploadData(bufferBuilder);
        dirty = false;
    }

    public void render() {
        vbo.bind();
        Shader.pointVertexAttribute(0, 3, 20, 0);
        Shader.enableVertexAttrib(0);
        Shader.pointVertexAttribute(2, 2, 20, 12);
        Shader.enableVertexAttrib(2);
        vbo.drawArrays(GL11.GL_TRIANGLES);
        vbo.unbind();
    }

    public World getWorld() {
        return world;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void dispose() {
        vbo.dispose();
    }
}
