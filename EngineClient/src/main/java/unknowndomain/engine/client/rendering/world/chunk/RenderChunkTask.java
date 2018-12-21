package unknowndomain.engine.client.rendering.world.chunk;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.client.rendering.block.BlockRenderer;
import unknowndomain.engine.client.rendering.block.ModelBlockRenderer;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.BlockPosIterator;
import unknowndomain.engine.util.ChunkCache;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public class RenderChunkTask {

    private final BlockRenderer blockRenderer = new ModelBlockRenderer();
    private final BufferBuilder bufferBuilder = new BufferBuilder(1048576);

    public void updateChunkMesh(ChunkMesh chunkMesh) {
        Chunk chunk = chunkMesh.getChunk();
        if (chunk.isEmpty()) {
            return;
        }
        ChunkCache chunkCache = createChunkCache(chunkMesh.getWorld(), chunkMesh.getChunkPos());
        BlockPosIterator blockPosIterator = BlockPosIterator.createFromChunkPos(chunkMesh.getChunkPos());
        bufferBuilder.begin(GL11.GL_TRIANGLES, true, true, true);
        while (blockPosIterator.hasNext()) {
            BlockPos pos = blockPosIterator.next();
            Block block = chunk.getBlock(pos);
            if (block == BlockAir.AIR) {
                continue;
            }

            blockRenderer.render(block, chunkCache, pos, bufferBuilder);
        }
        bufferBuilder.finish();
        chunkMesh.update(bufferBuilder);
        bufferBuilder.reset();
    }

    private ChunkCache createChunkCache(World world, ChunkPos middle) {
        return ChunkCache.create(world, middle.getX() - 1, middle.getY() - 1, middle.getZ() - 1,
                middle.getX() + 1, middle.getY() + 1, middle.getZ() + 1);
    }
}
