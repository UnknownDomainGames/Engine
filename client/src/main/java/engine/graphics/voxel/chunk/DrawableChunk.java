package engine.graphics.voxel.chunk;

import engine.graphics.Drawable;
import engine.graphics.Geometry;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.world.chunk.Chunk;

public final class DrawableChunk implements Drawable {

    private final Geometry geometry = new Geometry();

    private Chunk chunk;
    private SingleBufferMesh mesh;

    private volatile boolean dirty;
    private volatile boolean drawing;

    public DrawableChunk() {
        geometry.setDrawable(this);
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void uploadData(VertexDataBuf buf) {
        if (mesh == null) {
            mesh = SingleBufferMesh.builder().setDynamic().drawMode(DrawMode.TRIANGLES).build();
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

    @Override
    public void draw() {
        if (mesh == null) return;
        VoxelRenderHelper.getVoxelTextureAtlas().bind();
        mesh.draw();
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
