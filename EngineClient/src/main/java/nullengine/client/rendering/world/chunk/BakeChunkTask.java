package nullengine.client.rendering.world.chunk;

import nullengine.block.Block;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.math.BlockPos;
import nullengine.world.World;
import nullengine.world.chunk.Chunk;
import nullengine.world.util.BlockPosIterator;
import nullengine.world.util.ChunkCache;

public class BakeChunkTask implements Comparable<BakeChunkTask>, Runnable {

    private final ChunkRenderer chunkRenderer;
    private final ChunkMesh chunkMesh;
    private final double sqDistance;

    public BakeChunkTask(ChunkRenderer chunkRenderer, ChunkMesh chunkMesh, double sqDistance) {
        this.chunkRenderer = chunkRenderer;
        this.chunkMesh = chunkMesh;
        this.sqDistance = sqDistance;
    }

    @Override
    public void run() {
        chunkMesh.clearDirty();

        Chunk chunk = chunkMesh.getChunk();
        if (chunk.isAirChunk()) {
            return;
        }

        BlockRenderManager blockRenderManager = BlockRenderManager.instance();
        ChunkCache chunkCache = createChunkCache(chunk.getWorld(), chunk);
        BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunk(chunk);

        try {
            GLBuffer buffer = chunkRenderer.getBufferPool().get();
            buffer.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
            while (blockPosIterator.hasNext()) {
                BlockPos pos = blockPosIterator.next();
                Block block = chunkCache.getBlock(pos);
                blockRenderManager.generateMesh(block, chunkCache, pos, buffer);
            }
            buffer.finish();
            chunkRenderer.uploadChunk(chunkMesh, buffer);
        } catch (InterruptedException ignored) {
        }
    }

    private ChunkCache createChunkCache(World world, Chunk chunk) {
        return ChunkCache.create(world, chunk.getX() - 1, chunk.getY() - 1, chunk.getZ() - 1, chunk.getX() + 1, chunk.getY() + 1, chunk.getZ() + 1);
    }

    @Override
    public int compareTo(BakeChunkTask o) {
        return Double.compare(sqDistance, o.sqDistance);
    }
}
