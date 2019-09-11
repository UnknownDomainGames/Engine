package nullengine.world.chunk;

import nullengine.math.BlockPos;

public interface ChunkConstants {

    int SIZE_X = 16;
    int SIZE_Y = 16;
    int SIZE_Z = 16;

    int MAX_BLOCK_POS_X = SIZE_X - 1;
    int MAX_BLOCK_POS_Y = SIZE_Y - 1;
    int MAX_BLOCK_POS_Z = SIZE_Z - 1;

    int BITS_X = 4;
    int BITS_Y = 4;
    int BITS_Z = 4;

    static long getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return (abs(chunkX) << 42) | (abs(chunkY) << 21) | abs(chunkZ);
    }

    static long getChunkIndex(Chunk chunk) {
        return getChunkIndex(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ());
    }

    static long getChunkIndex(BlockPos blockPos) {
        return getChunkIndex(blockPos.x() >> BITS_X, blockPos.y() >> BITS_Y, blockPos.z() >> BITS_Z);
    }

    static BlockPos toChunkPos(BlockPos absPos) {
        return BlockPos.of(absPos.x() >> BITS_X, absPos.y() >> BITS_Y, absPos.z() >> BITS_Z);
    }

    long MAX_POSITIVE_CHUNK_POS = (1 << 20) - 1;

    private static long abs(long value) {
        return value >= 0 ? value : MAX_POSITIVE_CHUNK_POS - value;
    }
}
