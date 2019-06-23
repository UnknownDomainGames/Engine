package unknowndomain.engine.world.storage;

import unknowndomain.engine.world.World;

import java.nio.file.Path;

public interface WorldLoader<T extends World> {

    Path getStoragePath();

    T loadWorld();

    void saveWorld();

    void flush();
}
