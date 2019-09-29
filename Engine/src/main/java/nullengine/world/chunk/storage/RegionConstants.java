package nullengine.world.chunk.storage;

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
}
