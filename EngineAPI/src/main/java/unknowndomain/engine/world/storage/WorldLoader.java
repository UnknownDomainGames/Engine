package unknowndomain.engine.world.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.storage.ChunkStorer;

import java.nio.file.Path;

public interface WorldLoader<T extends World> {
    void saveWorld();

    Path getStorageLocation();

    T loadWorld();

    void flush();
}
