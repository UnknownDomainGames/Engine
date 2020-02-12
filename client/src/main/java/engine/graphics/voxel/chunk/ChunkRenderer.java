package engine.graphics.voxel.chunk;

import engine.event.Listener;
import engine.event.Order;
import engine.event.block.BlockChangeEvent;
import engine.event.world.chunk.ChunkLoadEvent;
import engine.event.world.chunk.ChunkUnloadEvent;
import engine.graphics.GraphicsEngine;
import engine.graphics.RenderManager;
import engine.graphics.Scene3D;
import engine.graphics.viewport.Viewport;
import engine.math.BlockPos;
import engine.world.World;
import engine.world.chunk.Chunk;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import java.util.LinkedList;
import java.util.Queue;

import static engine.world.chunk.ChunkConstants.*;

public final class ChunkRenderer {

    private final LongObjectMap<DrawableChunk> chunks = new LongObjectHashMap<>();
    private final Queue<DrawableChunk> recycleChunks = new LinkedList<>();

    private final Scene3D scene;
    private final Viewport viewport;
    private final World world;

    public ChunkRenderer(RenderManager manager, World world) {
        this.scene = manager.getScene();
        this.viewport = manager.getViewport();
        this.world = world;
        world.getLoadedChunks().forEach(this::addChunk);
        manager.getEngine().getEventBus().register(this);
    }

    @Listener(order = Order.LAST)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (world.equals(chunk.getWorld())) {
            GraphicsEngine.getGraphicsBackend().submitTask(() -> addChunk(chunk));
        }
    }

    @Listener(order = Order.LAST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (world.equals(chunk.getWorld())) {
            GraphicsEngine.getGraphicsBackend().submitTask(() -> removeChunk(chunk));
        }
    }

    private void addChunk(Chunk chunk) {
        long index = getChunkIndex(chunk);
        DrawableChunk drawableChunk = recycleChunks.poll();
        if (drawableChunk == null) drawableChunk = new DrawableChunk();
        drawableChunk.setChunk(chunk);
        chunks.put(index, drawableChunk);
        scene.addNode(drawableChunk.getGeometry());
        markChunkDirty(drawableChunk);
    }

    private void markChunkDirty(DrawableChunk chunk) {
        if (chunk == null || chunk.isDirty()) return;
        chunk.markDirty();

        if (chunk.isDrawing()) return;
        ChunkBaker.execute(new ChunkBaker.Task(chunk, distanceSqChunkToCamera(chunk)));
    }

    private void markChunkDirty(long index) {
        markChunkDirty(chunks.get(index));
    }

    private double distanceSqChunkToCamera(DrawableChunk chunk) {
        Vector3fc position = viewport.getCamera().getPosition();
        Vector3ic center = chunk.getChunk().getCenter();
        return position.distanceSquared(center.x(), center.y(), center.z());
    }

    private void removeChunk(Chunk chunk) {
        long index = getChunkIndex(chunk);
        DrawableChunk drawableChunk = chunks.get(index);
        scene.removeNode(drawableChunk.getGeometry());
        drawableChunk.setChunk(null);
        recycleChunks.add(drawableChunk);
    }

    @Listener(order = Order.LAST)
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos().toUnmodifiable();
        int chunkX = pos.x() >> CHUNK_X_BITS,
                chunkY = pos.y() >> CHUNK_Y_BITS,
                chunkZ = pos.z() >> CHUNK_Z_BITS;
        markChunkDirty(getChunkIndex(event.getPos()));

        // Mark neighbor chunk dirty.
        int chunkW = pos.x() + 1 >> CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.x() - 1 >> CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.y() + 1 >> CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.y() - 1 >> CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.z() + 1 >> CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
        chunkW = pos.z() - 1 >> CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
    }
}
