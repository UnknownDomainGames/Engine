package engine.graphics.world.chunk;

import engine.block.Block;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.math.BlockPos;
import engine.world.World;
import engine.world.chunk.Chunk;
import engine.world.util.BlockPosIterator;
import engine.world.util.ChunkCache;

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
            VertexDataBuf buffer = chunkRenderer.getBufferPool().get();
            buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
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
