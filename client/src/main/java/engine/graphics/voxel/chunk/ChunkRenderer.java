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
import org.joml.Vector3ic;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
        if (disposed) {
            return;
        }

        Vector3ic chunkPos = chunk.getPos();
        int chunkX = chunkPos.x();
        int chunkY = chunkPos.y();
        int chunkZ = chunkPos.z();
        long index = Chunk.index(chunkX, chunkY, chunkZ);

        if (chunks.containsKey(index)) {
            return;
        }

        DrawableChunk drawableChunk = recycleChunks.poll();
        if (drawableChunk == null) {
            drawableChunk = new DrawableChunk(this);
        }
        drawableChunk.setChunk(chunk);
        chunks.put(index, drawableChunk);
        scene.addNode(drawableChunk);
        drawableChunk.markDirty();

        // Mark neighbor chunks dirty.
        markChunkDirty(Chunk.index(chunkX + 1, chunkY, chunkZ));
        markChunkDirty(Chunk.index(chunkX - 1, chunkY, chunkZ));
        markChunkDirty(Chunk.index(chunkX, chunkY + 1, chunkZ));
        markChunkDirty(Chunk.index(chunkX, chunkY - 1, chunkZ));
        markChunkDirty(Chunk.index(chunkX, chunkY, chunkZ + 1));
        markChunkDirty(Chunk.index(chunkX, chunkY, chunkZ - 1));
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
        new ArrayList<>(chunks.values()).forEach(this::removeChunk);
    }

    @Listener(order = Order.LAST)
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos();
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        int chunkX = x >> Chunk.CHUNK_X_BITS;
        int chunkY = y >> Chunk.CHUNK_Y_BITS;
        int chunkZ = z >> Chunk.CHUNK_Z_BITS;

        markChunkDirty(Chunk.index(chunkX, chunkY, chunkZ));

        // Mark neighbor chunks dirty.
        int chunkW = (x + 1) >> Chunk.CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(Chunk.index(chunkW, chunkY, chunkZ));
        }
        chunkW = (x - 1) >> Chunk.CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(Chunk.index(chunkW, chunkY, chunkZ));
        }
        chunkW = (y + 1) >> Chunk.CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(Chunk.index(chunkX, chunkW, chunkZ));
        }
        chunkW = (y - 1) >> Chunk.CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(Chunk.index(chunkX, chunkW, chunkZ));
        }
        chunkW = (z + 1) >> Chunk.CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(Chunk.index(chunkX, chunkY, chunkW));
        }
        chunkW = (z - 1) >> Chunk.CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(Chunk.index(chunkX, chunkY, chunkW));
        }
    }

    private void markChunkDirty(long index) {
        DrawableChunk chunk = chunks.get(index);
        if (chunk != null) {
            chunk.markDirty();
        }
    }
}
