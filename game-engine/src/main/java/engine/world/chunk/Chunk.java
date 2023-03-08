package engine.world.chunk;

import engine.block.state.BlockState;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;

public interface Chunk {
    int CHUNK_X_BITS = 5;
    int CHUNK_Y_BITS = 5;
    int CHUNK_Z_BITS = 5;

    int CHUNK_X_SIZE = 1 << CHUNK_X_BITS;
    int CHUNK_Y_SIZE = 1 << CHUNK_Y_BITS;
    int CHUNK_Z_SIZE = 1 << CHUNK_Z_BITS;

    int CHUNK_MAX_X = CHUNK_X_SIZE - 1;
    int CHUNK_MAX_Y = CHUNK_Y_SIZE - 1;
    int CHUNK_MAX_Z = CHUNK_Z_SIZE - 1;

    int BLOCK_COUNT = CHUNK_X_SIZE * CHUNK_Y_SIZE * CHUNK_Z_SIZE;

    static Vector3i fromWorldPos(int x, int y, int z) {
        return new Vector3i(x >> CHUNK_X_BITS, y >> CHUNK_Y_BITS, z >> CHUNK_Z_BITS);
    }

    static Vector3i fromWorldPos(Vector3ic v) {
        return fromWorldPos(v.x(), v.y(), v.z());
    }

    static Vector3i fromWorldPos(float x, float y, float z) {
        return fromWorldPos((int) x, (int) y, (int) z);
    }

    static Vector3i fromWorldPos(Vector3fc v) {
        return fromWorldPos((int) v.x(), (int) v.y(), (int) v.z());
    }

    static Vector3i fromWorldPos(double x, double y, double z) {
        return fromWorldPos((int) x, (int) y, (int) z);
    }

    static Vector3i fromWorldPos(Vector3dc v) {
        return fromWorldPos((int) v.x(), (int) v.y(), (int) v.z());
    }

    static long index(int chunkX, int chunkY, int chunkZ) {
        return (toUnsigned(chunkY) << 42) | (toUnsigned(chunkZ) << 21) | toUnsigned(chunkX);
    }

    static long index(Vector3ic chunkPos) {
        return index(chunkPos.x(), chunkPos.y(), chunkPos.z());
    }

    static long index(Chunk chunk) {
        return index(chunk.getPos());
    }

    static long indexFromWorldPos(int x, int y, int z) {
        return index(x >> CHUNK_X_BITS, y >> CHUNK_Y_BITS, z >> CHUNK_Z_BITS);
    }

    static long indexFromWorldPos(Vector3ic blockPos) {
        return indexFromWorldPos(blockPos.x(), blockPos.y(), blockPos.z());
    }

    private static long toUnsigned(int value) {
        return value & 0x1fffff;
    }

    ChunkStatus getStatus();

    @Nonnull
    World getWorld();

    @Nonnull
    Vector3ic getPos();

    int getX();

    int getY();

    int getZ();

    @Nonnull
    Vector3ic getMin();

    @Nonnull
    Vector3ic getMax();

    @Nonnull
    Vector3ic getCenter();

    /**
     * Get block in a specific path
     *
     * @param x x-coordinate of the block related to chunk coordinate system
     * @param y y-coordinate of the block related to chunk coordinate system
     * @param z z-coordinate of the block related to chunk coordinate system
     * @return the block in the specified path
     */
    BlockState getBlock(int x, int y, int z);

    default BlockState getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

//    int getBlockId(int x, int y, int z);

//    default int getBlockId(@Nonnull BlockPos pos) {
//        return getBlockId(pos.x(), pos.y(), pos.z());
//    }

    BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause);

    boolean isAirChunk();
}
