package unknowndomain.engine.world.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.storage.ChunkReader;

import java.io.File;

public interface WorldLoader {
    void saveWorld(World world);

    File getStorageLocation();

    World loadWorld();

    void flush();

    ChunkReader getChunkLoader();
}
