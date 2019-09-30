package nullengine.world.chunk;

import nullengine.math.BlockPos;

public interface ChunkConstants {

    int CHUNK_X_BITS = 4;
    int CHUNK_Y_BITS = 4;
    int CHUNK_Z_BITS = 4;

    int CHUNK_X_SIZE = 1 << CHUNK_X_BITS;
    int CHUNK_Y_SIZE = 1 << CHUNK_Y_BITS;
    int CHUNK_Z_SIZE = 1 << CHUNK_Z_BITS;

    int CHUNK_MAX_X = CHUNK_X_SIZE - 1;
    int CHUNK_MAX_Y = CHUNK_Y_SIZE - 1;
    int CHUNK_MAX_Z = CHUNK_Z_SIZE - 1;

    int BLOCK_COUNT = CHUNK_X_SIZE * CHUNK_Y_SIZE * CHUNK_Z_SIZE;

    static long getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return (toUnsigned(chunkX) << 42) | (toUnsigned(chunkY) << 21) | toUnsigned(chunkZ);
    }

    static long getChunkIndex(Chunk chunk) {
        return getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ());
    }

    static long getChunkIndex(BlockPos blockPos) {
        return getChunkIndex(blockPos.x() >> CHUNK_X_BITS, blockPos.y() >> CHUNK_Y_BITS, blockPos.z() >> CHUNK_Z_BITS);
    }

    private static long toUnsigned(int value) {
        return value & 0x1fffff;
    }
}
