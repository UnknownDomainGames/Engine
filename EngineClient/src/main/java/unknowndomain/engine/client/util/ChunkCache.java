package unknowndomain.engine.client.util;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.BlockAccessor;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class ChunkCache implements BlockAccessor {

    public static ChunkCache create(World world, ChunkPos from, ChunkPos to) {
        int xLength = to.getX() - from.getX() + 1;
        int yLength = to.getY() - from.getY() + 1;
        int zLength = to.getZ() - from.getZ() + 1;
        Chunk[][][] chunks = new Chunk[xLength][yLength][zLength];
        for (int x = from.getX(); x <= to.getX(); x++) {
            for (int y = from.getY(); y <= to.getY(); y++) {
                for (int z = from.getZ(); y <= to.getZ(); z++) {
                    chunks[x - from.getX()][y - from.getY()][z - from.getZ()] = world.getChunk(x, y, z);
                }
            }
        }
        return new ChunkCache(world, from.getX(), from.getY(), from.getZ(), chunks);
    }

    private final World world;
    private final int chunkX;
    private final int chunkY;
    private final int chunkZ;
    private final Chunk[][][] chunks;

    private ChunkCache(World world, int chunkX, int chunkY, int chunkZ, Chunk[][][] chunks) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.chunks = chunks;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    @Nonnull
    public Block getBlock(int x, int y, int z) {
        int chunkX = (x >> 4) - this.chunkX, chunkY = (y >> 4) - this.chunkY, chunkZ = (z >> 4) - this.chunkZ;
        if (chunkX >= 0 && chunkX < chunks.length && chunkY >= 0 && chunkY < chunks[chunkX].length && chunkZ >= 0 && chunkZ < chunks[chunkX][chunkY].length) {
            return chunks[chunkX][chunkY][chunkZ].getBlock(x, y, z);
        }
        return world.getBlock(x, y, z);
    }

    @Nonnull
    @Override
    public Block setBlock(int x, int y, int z, @Nonnull Block block) {
        int chunkX = (x >> 4) - this.chunkX, chunkY = (y >> 4) - this.chunkY, chunkZ = (z >> 4) - this.chunkZ;
        if (chunkX >= 0 && chunkX < chunks.length && chunkY >= 0 && chunkY < chunks[chunkX].length && chunkZ >= 0 && chunkZ < chunks[chunkX][chunkY].length) {
            return chunks[chunkX][chunkY][chunkZ].setBlock(x, y, z, block);
        }
        return world.setBlock(x, y, z, block);
    }
}
