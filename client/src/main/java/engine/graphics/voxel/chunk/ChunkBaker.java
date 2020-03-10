package engine.graphics.voxel.chunk;

import engine.block.Block;
import engine.graphics.GraphicsEngine;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexDataBufPool;
import engine.graphics.vertex.VertexFormat;
import engine.math.BlockPos;
import engine.world.BlockGetter;
import engine.world.World;
import engine.world.chunk.Chunk;
import engine.world.util.BlockPosIterator;
import engine.world.util.ChunkCache;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChunkBaker {

    private static ThreadPoolExecutor executor;
    private static VertexDataBufPool dataBufPool;

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
        dataBufPool = VertexDataBufPool.create(0x200000, threadCount * 8);
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
                if (chunk.isAirChunk()) return;

                BlockRenderManager blockRenderManager = BlockRenderManager.instance();
                VertexDataBuf buf = dataBufPool.get();
                buf.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
                BlockGetter blockCache = createChunkCache(chunk.getWorld(), chunk);
                BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunk(chunk);
                while (blockPosIterator.hasNext()) {
                    BlockPos pos = blockPosIterator.next();
                    Block block = blockCache.getBlock(pos);
                    blockRenderManager.generateMesh(block, blockCache, pos, buf);
                }
                buf.finish();

                GraphicsEngine.getGraphicsBackend().submitTask(() -> {
                    drawableChunk.finishBake(buf);
                    dataBufPool.free(buf);
                    if (drawableChunk.isDisposed()) return;
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
