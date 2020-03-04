package engine.graphics.voxel.chunk;

import engine.graphics.Geometry;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.world.chunk.Chunk;

public final class DrawableChunk extends Geometry {

    private Chunk chunk;
    private SingleBufMesh mesh;

    private volatile boolean dirty;
    private volatile boolean drawing;

    public DrawableChunk() {
        setTexture(VoxelRenderHelper.getVoxelTextureAtlas().getTexture());
        setVisible(false);
    }

    public void uploadData(VertexDataBuf buf) {
        if (mesh == null) {
            mesh = SingleBufMesh.builder().setDynamic().drawMode(DrawMode.TRIANGLES).build();
            setMesh(mesh);
            setVisible(true);
        }
        mesh.uploadData(buf);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }
}
