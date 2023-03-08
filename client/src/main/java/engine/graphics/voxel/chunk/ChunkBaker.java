package engine.graphics.voxel.chunk;

import engine.block.state.BlockState;
import engine.graphics.GraphicsEngine;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.queue.RenderType;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexDataBufferPool;
import engine.graphics.vertex.VertexFormat;
import engine.math.BlockPos;
import engine.world.BlockGetter;
import engine.world.World;
import engine.world.chunk.Chunk;
import engine.world.util.BlockPosIterator;
import engine.world.util.ChunkCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChunkBaker {
    private static ThreadPoolExecutor executor;
    private static VertexDataBufferPool bufferPool;

    public static void start() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Chunk Baker " + poolNumber.getAndIncrement());
            }
        });
        bufferPool = VertexDataBufferPool.create(0x200000, threadCount * 8);
    }

    public static void stop() {
        executor.shutdownNow();
    }

    public static void execute(Task task) {
        executor.execute(task);
    }

    private ChunkBaker() {
    }

    public static final class Task implements Runnable, Comparable<Task> {

        private final DrawableChunk drawableChunk;
        private final double sqDistance;

        public Task(DrawableChunk drawableChunk, double sqDistance) {
            this.drawableChunk = drawableChunk;
            this.sqDistance = sqDistance;
        }

        @Override
        public void run() {
            try {
                drawableChunk.clearDirty();
                Chunk chunk = drawableChunk.getChunk();
                if (chunk == null || chunk.isAirChunk()) {
                    drawableChunk.terminateBake();
                    return;
                }
                BlockRenderManager blockRenderManager = BlockRenderManager.instance();
                Map<RenderType, VertexDataBuffer> buffers = new HashMap<>();
                BlockGetter blockCache = createChunkCache(chunk.getWorld(), chunk);
                BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunk(chunk);
                while (blockPosIterator.hasNext()) {
                    BlockPos pos = blockPosIterator.next();
                    BlockState block = blockCache.getBlock(pos);
                    var renderType = ((BlockRenderManagerImpl) blockRenderManager).getBlockRenderTypeMap().get(block.getPrototype());
                    if (renderType == RenderType.TRANSPARENT)
                        renderType = RenderType.TRANSLUCENT; //TODO: when transparent and translucent handles differently, remove it
                    var buffer = buffers.get(renderType);
                    if (buffer == null) {
                        buffer = bufferPool.get();
                        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
                        buffers.put(renderType, buffer);
                    }
                    blockRenderManager.generateMesh(block, blockCache, pos, buffer);
                }
                buffers.values().forEach(VertexDataBuffer::finish);

                GraphicsEngine.getGraphicsBackend().runLater(() -> {
                    if (drawableChunk.isDisposed()) return;
                    drawableChunk.finishBake(buffers);
                    buffers.values().forEach(buf -> bufferPool.free(buf));
                    if (drawableChunk.isDirty()) drawableChunk.executeBake();
                });
            } catch (InterruptedException ignored) {
            }
        }

        private ChunkCache createChunkCache(World world, Chunk chunk) {
            return ChunkCache.create(world,
                    chunk.getX() - 1, chunk.getY() - 1, chunk.getZ() - 1,
                    chunk.getX() + 1, chunk.getY() + 1, chunk.getZ() + 1);
        }

        @Override
        public int compareTo(Task o) {
            return Double.compare(sqDistance, o.sqDistance);
        }
    }
}
