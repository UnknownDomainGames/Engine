package unknowndomain.engine.client.rendering.world.chunk;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
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

        ChunkCache chunkCache = createChunkCache(chunk.getWorld(), chunkMesh.getChunkPos());
        BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunkPos(chunkMesh.getChunkPos());

        BufferBuilder buffer = ((BakeChunkThread) Thread.currentThread()).getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, true, true, true);
        while (blockPosIterator.hasNext()) {
            BlockPos pos = blockPosIterator.next();
            Block block = chunk.getBlock(pos);
            if (block == BlockAir.AIR) {
                continue;
            }

            chunkRenderer.getBlockRenderer().render(block, chunkCache, pos, buffer);
        }
        buffer.finish();
        chunkRenderer.upload(chunkMesh, buffer);
        buffer.reset();
    }

    private ChunkCache createChunkCache(World world, ChunkPos middle) {
        return ChunkCache.create(world, middle.getX() - 1, middle.getY() - 1, middle.getZ() - 1,
                middle.getX() + 1, middle.getY() + 1, middle.getZ() + 1);
    }

    @Override
    public int compareTo(BakeChunkTask o) {
        return Double.compare(sqDistance, o.sqDistance);
    }
}
