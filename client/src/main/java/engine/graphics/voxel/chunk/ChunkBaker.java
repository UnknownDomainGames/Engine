package engine.graphics.voxel.chunk;

import engine.block.state.BlockState;
import engine.graphics.GraphicsEngine;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.queue.RenderType;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexDataBufPool;
import engine.graphics.vertex.VertexFormat;
import engine.math.BlockPos;
import engine.world.BlockGetter;
import engine.world.World;
import engine.world.chunk.Chunk;
import engine.world.util.BlockPosIterator;
import engine.world.util.ChunkCache;

import java.util.HashMap;
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
                if (chunk == null || chunk.isAirChunk()) {
                    drawableChunk.terminateBake();
                    return;
                }
                BlockRenderManager blockRenderManager = BlockRenderManager.instance();
                var bufs = new HashMap<RenderType, VertexDataBuf>();
                bufs.put(RenderType.OPAQUE, dataBufPool.get());
                bufs.put(RenderType.TRANSLUCENT, dataBufPool.get());
//                bufs.put(RenderType.TRANSPARENT, dataBufPool.get()); //TODO: when transparent and translucent handles differently, use it
                bufs.forEach((type, buf) -> buf.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL));
                BlockGetter blockCache = createChunkCache(chunk.getWorld(), chunk);
                BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunk(chunk);
                while (blockPosIterator.hasNext()) {
                    BlockPos pos = blockPosIterator.next();
                    BlockState block = blockCache.getBlock(pos);
                    var renderType = ((BlockRenderManagerImpl) blockRenderManager).getBlockRenderTypeMap().get(block.getPrototype());
                    if (renderType == RenderType.TRANSPARENT)
                        renderType = RenderType.TRANSLUCENT; //TODO: when transparent and translucent handles differently, remove it
                    if (!bufs.containsKey(renderType)) continue;
                    blockRenderManager.generateMesh(block, blockCache, pos, bufs.get(renderType));
                }
                bufs.values().forEach(VertexDataBuf::finish);

                GraphicsEngine.getGraphicsBackend().submitTask(() -> {
                    drawableChunk.finishBake(bufs);
                    bufs.values().forEach(buf -> dataBufPool.free(buf));
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
