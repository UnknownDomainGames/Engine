package engine.world.chunk.storage;

public interface RegionConstants {

    int REGION_X_BITS = 4;
    int REGION_Y_BITS = 4;
    int REGION_Z_BITS = 4;

    int REGION_X_SIZE = 16;
    int REGION_Y_SIZE = 16;
    int REGION_Z_SIZE = 16;

    int REGION_MAX_X = REGION_X_SIZE - 1;
    int REGION_MAX_Y = REGION_Y_SIZE - 1;
    int REGION_MAX_Z = REGION_Z_SIZE - 1;

    int REGION_SIZE = REGION_X_SIZE * REGION_Y_SIZE * REGION_Z_SIZE;

    static long getRegionIndex(int chunkX, int chunkY, int chunkZ) {
        return (toUnsigned(chunkX >> REGION_X_BITS) << 42) |
                (toUnsigned(chunkY >> REGION_Y_BITS) << 21) |
                toUnsigned(chunkZ >> REGION_Z_BITS);
    }

    static int toRegionCoordinate(int coord) {
        return coord >> REGION_X_BITS; // Assumption: the bit offset of all axis are the same
    }

    private static long toUnsigned(int value) {
        return value & 0x1fffff;
    }
}
