package engine.graphics.voxel.chunk;

import engine.Platform;
import engine.event.Listener;
import engine.event.Order;
import engine.event.block.BlockChangeEvent;
import engine.event.world.chunk.ChunkLoadEvent;
import engine.event.world.chunk.ChunkUnloadEvent;
import engine.graphics.GraphicsEngine;
import engine.graphics.GraphicsManager;
import engine.graphics.Scene3D;
import engine.graphics.viewport.Viewport;
import engine.math.BlockPos;
import engine.world.World;
import engine.world.chunk.Chunk;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public final class ChunkRenderer {

    private final LongObjectMap<DrawableChunk> chunks = new LongObjectHashMap<>();
    private final Queue<DrawableChunk> recycleChunks = new ArrayDeque<>();

    private final Scene3D scene;
    private final Viewport viewport;
    private final World world;

    private volatile boolean disposed;

    public ChunkRenderer(GraphicsManager manager, World world) {
        this.scene = manager.getScene();
        this.viewport = manager.getViewport();
        this.world = world;
        ChunkBaker.start();
        world.getLoadedChunks().forEach(this::addChunk);
        Platform.getEngine().getEventBus().register(this);
    }

    public World getWorld() {
        return world;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public boolean isEqualsWorld(World world) {
        return this.world.equals(world);
    }

    @Listener(order = Order.LAST)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (isEqualsWorld(chunk.getWorld())) {
            GraphicsEngine.getGraphicsBackend().runLater(() -> addChunk(chunk));
        }
    }

    private void addChunk(Chunk chunk) {
        if (disposed) return;
        long index = Chunk.index(chunk);
        if (chunks.containsKey(index)) return;
        DrawableChunk drawableChunk = recycleChunks.poll();
        if (drawableChunk == null) {
            drawableChunk = new DrawableChunk(this);
        }
        drawableChunk.setChunk(chunk);
        chunks.put(index, drawableChunk);
        scene.addNode(drawableChunk);
        drawableChunk.markDirty();
    }

    @Listener(order = Order.LAST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (isEqualsWorld(chunk.getWorld())) {
            GraphicsEngine.getGraphicsBackend().runLater(() -> removeChunk(chunk));
        }
    }

    private void removeChunk(Chunk chunk) {
        long chunkIndex = Chunk.index(chunk);
        DrawableChunk removed = chunks.remove(chunkIndex);
        if (removed == null) return;
        removed.reset();
        scene.removeNode(removed);
        recycleChunks.add(removed);
    }

    private void removeChunk(DrawableChunk chunk) {
        removeChunk(chunk.getChunk());
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void dispose() {
        if (disposed) return;
        disposed = true;
        ChunkBaker.stop();
        Platform.getEngine().getEventBus().unregister(this);
        List.copyOf(chunks.values()).forEach(this::removeChunk);
    }

    @Listener(order = Order.LAST)
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos();
        int chunkX = pos.x() >> Chunk.CHUNK_X_BITS,
                chunkY = pos.y() >> Chunk.CHUNK_Y_BITS,
                chunkZ = pos.z() >> Chunk.CHUNK_Z_BITS;
        markChunkDirty(Chunk.indexFromWorldPos(event.getPos()));

        // Mark neighbor chunk dirty.
        int chunkW = pos.x() + 1 >> Chunk.CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(Chunk.index(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.x() - 1 >> Chunk.CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(Chunk.index(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.y() + 1 >> Chunk.CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(Chunk.index(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.y() - 1 >> Chunk.CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(Chunk.index(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.z() + 1 >> Chunk.CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(Chunk.index(chunkX, chunkY, chunkW));
        }
        chunkW = pos.z() - 1 >> Chunk.CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(Chunk.index(chunkX, chunkY, chunkW));
        }
    }

    private void markChunkDirty(long index) {
        chunks.get(index).markDirty();
    }
}
