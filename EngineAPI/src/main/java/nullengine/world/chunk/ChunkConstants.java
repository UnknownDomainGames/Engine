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
        return (toUnsigned(chunkX) << 42) | (toUnsigned(chunkY) << 21) | toUnsigned(chunkZ);
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

    private static long toUnsigned(int value) {
        return value & 0x1fffff;
    }
}
