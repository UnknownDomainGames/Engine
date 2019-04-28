package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.BlockPosIterator;
import unknowndomain.engine.util.ChunkCache;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

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
        chunkMesh.startBake();

        Chunk chunk = chunkMesh.getChunk();
        if (chunk.isAirChunk()) {
            return;
        }

        ChunkCache chunkCache = createChunkCache(chunk.getWorld(), chunk);
        BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunk(chunk);

        GLBuffer buffer = ((BakeChunkThread) Thread.currentThread()).getBuffer();
        buffer.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
        while (blockPosIterator.hasNext()) {
            BlockPos pos = blockPosIterator.next();
            ClientBlock block = chunkRenderer.getClientBlockRegistry().getValue(chunkCache.getBlockId(pos));
            if (block.isVisible()) {
                block.getRenderer().generate(block, chunkCache, pos, buffer);
            }
        }
        buffer.finish();
        chunkRenderer.upload(chunkMesh, buffer);
        buffer.reset();
    }

    private ChunkCache createChunkCache(World world, Chunk chunk) {
        return ChunkCache.create(world, chunk.getChunkX() - 1, chunk.getChunkY() - 1, chunk.getChunkZ() - 1, chunk.getChunkX() + 1, chunk.getChunkY() + 1, chunk.getChunkZ() + 1);
    }

    @Override
    public int compareTo(BakeChunkTask o) {
        return Double.compare(sqDistance, o.sqDistance);
    }
}
