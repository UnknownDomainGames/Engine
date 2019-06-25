package nullengine.world.storage;

import nullengine.world.World;

import java.nio.file.Path;

public interface WorldLoader<T extends World> {

    Path getStoragePath();

    T loadWorld();

    void saveWorld();

    void flush();
}
