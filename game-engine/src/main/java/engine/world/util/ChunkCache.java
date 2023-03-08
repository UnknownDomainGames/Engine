package engine.world.util;

import engine.block.state.BlockState;
import engine.registry.Registries;
import engine.world.BlockGetter;
import engine.world.World;
import engine.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class ChunkCache implements BlockGetter {

    public static ChunkCache create(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        int xLength = toX - fromX + 1;
        int yLength = toY - fromY + 1;
        int zLength = toZ - fromZ + 1;
        Chunk[][][] chunks = new Chunk[xLength][yLength][zLength];
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    chunks[x - fromX][y - fromY][z - fromZ] = world.getChunk(x, y, z, false);
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
    public BlockState getBlock(int x, int y, int z) {
        int chunkX = (x >> Chunk.CHUNK_X_BITS) - this.chunkX;
        int chunkY = (y >> Chunk.CHUNK_Y_BITS) - this.chunkY;
        int chunkZ = (z >> Chunk.CHUNK_Z_BITS) - this.chunkZ;
        if (chunkX >= 0 && chunkX < chunks.length &&
                chunkY >= 0 && chunkY < chunks[chunkX].length &&
                chunkZ >= 0 && chunkZ < chunks[chunkX][chunkY].length) {
            Chunk chunk = chunks[chunkX][chunkY][chunkZ];
            return chunk == null ? Registries.getBlockRegistry().air().getDefaultState() : chunk.getBlock(x, y, z); // FIXME:
        }
        return Registries.getBlockRegistry().air().getDefaultState();
//        return world.getBlock(x, y, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return getBlock(x, y, z) == Registries.getBlockRegistry().air().getDefaultState();
    }
}
