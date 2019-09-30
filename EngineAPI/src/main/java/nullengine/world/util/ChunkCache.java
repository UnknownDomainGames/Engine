package nullengine.world.util;

import nullengine.block.Block;
import nullengine.registry.Registries;
import nullengine.world.BlockGetter;
import nullengine.world.World;
import nullengine.world.chunk.Chunk;

import javax.annotation.Nonnull;

import static nullengine.world.chunk.ChunkConstants.*;

public class ChunkCache implements BlockGetter {

    public static ChunkCache create(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        int xLength = toX - fromX + 1;
        int yLength = toY - fromY + 1;
        int zLength = toZ - fromZ + 1;
        Chunk[][][] chunks = new Chunk[xLength][yLength][zLength];
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    chunks[x - fromX][y - fromY][z - fromZ] = world.getChunk(x, y, z);
                }
            }
        }
        return new ChunkCache(world, fromX, fromY, fromZ, chunks);
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
        int chunkX = (x >> CHUNK_X_BITS) - this.chunkX;
        int chunkY = (y >> CHUNK_Y_BITS) - this.chunkY;
        int chunkZ = (z >> CHUNK_Z_BITS) - this.chunkZ;
        if (chunkX >= 0 && chunkX < chunks.length &&
                chunkY >= 0 && chunkY < chunks[chunkX].length &&
                chunkZ >= 0 && chunkZ < chunks[chunkX][chunkY].length) {
            Chunk chunk = chunks[chunkX][chunkY][chunkZ];
            return chunk == null ? Registries.getBlockRegistry().air() : chunk.getBlock(x, y, z); // FIXME:
        }
        return world.getBlock(x, y, z);
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        int chunkX = (x >> CHUNK_X_BITS) - this.chunkX;
        int chunkY = (y >> CHUNK_Y_BITS) - this.chunkY;
        int chunkZ = (z >> CHUNK_Z_BITS) - this.chunkZ;
        if (chunkX >= 0 && chunkX < chunks.length && chunkY >= 0 && chunkY < chunks[chunkX].length && chunkZ >= 0 && chunkZ < chunks[chunkX][chunkY].length) {
            Chunk chunk = chunks[chunkX][chunkY][chunkZ];
            return chunk == null ? Registries.getBlockRegistry().air().getId() : chunk.getBlockId(x, y, z); // FIXME:
        }
        return world.getBlockId(x, y, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return getBlock(x, y, z) == Registries.getBlockRegistry().air();
    }
}
