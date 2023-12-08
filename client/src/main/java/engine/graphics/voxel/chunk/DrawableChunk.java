package engine.graphics.voxel.chunk;

import engine.graphics.Geometry;
import engine.graphics.bounds.BoundingBox;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.queue.RenderType;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.voxel.VoxelGraphicsHelper;
import engine.world.chunk.Chunk;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DrawableChunk extends Geometry {

    private final ChunkRenderer renderer;
    private final BoundingBox boundingBox = new BoundingBox();

    private Chunk chunk;

    private Map<RenderType, DrawableChunkPiece> pieces;

    private volatile boolean dirty;
    private boolean baking;

    public DrawableChunk(ChunkRenderer renderer) {
        this.renderer = renderer;
        setTexture(VoxelGraphicsHelper.getVoxelTextureAtlas().getTexture());
        setVisible(false);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
        Vector3ic min = chunk.getMin();
        Vector3ic max = chunk.getMax();
        this.boundingBox.setMin(min.x(), min.y(), min.z()).setMax(max.x(), max.y(), max.z());
    }

    public void reset() {
        this.chunk = null;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void markDirty() {
        dirty = true;
        if (!baking) executeBake();
    }

    public void executeBake() {
        if (chunk == null) return; // stop baking if there is nothing to bake
        baking = true;
        ChunkBaker.execute(new ChunkBaker.Task(this, distanceSqChunkToCamera()));
    }

    private double distanceSqChunkToCamera() {
        Vector3fc position = renderer.getViewport().getCamera().getPosition();
        Vector3ic center = chunk.getCenter();
        return position.distanceSquared(center.x(), center.y(), center.z());
    }

    public void finishBake(Map<RenderType, VertexDataBuffer> buffers) {
        if (pieces == null) {
            pieces = new HashMap<>();
        }

        buffers.forEach((renderType, buffer) -> pieces.computeIfAbsent(renderType, this::createPiece).uploadData(buffer));

        Set<RenderType> invisibleRenderTypes = new HashSet<>(pieces.keySet());
        invisibleRenderTypes.removeAll(buffers.keySet());
        for (RenderType invisibleRenderType : invisibleRenderTypes) {
            pieces.get(invisibleRenderType).setVisible(false);
        }

        baking = false;
    }

    private DrawableChunkPiece createPiece(RenderType renderType) {
        var piece = new DrawableChunkPiece(renderType);
        piece.setTexture(getTexture());
        piece.setBounds(boundingBox);
        addChild(piece);
        return piece;
    }

    public void terminateBake() {
        baking = false;
    }

    public boolean isDisposed() {
        return renderer.isDisposed();
    }

    private static class DrawableChunkPiece extends Geometry {
        private final SingleBufMesh mesh;

        private DrawableChunkPiece(RenderType type) {
            mesh = SingleBufMesh.builder().setDynamic().drawMode(DrawMode.TRIANGLES).build();
            setRenderType(type);
            setMesh(mesh);
        }

        public void uploadData(VertexDataBuffer buffer) {
            mesh.uploadData(buffer);
            setVisible(true);
        }
    }
}
