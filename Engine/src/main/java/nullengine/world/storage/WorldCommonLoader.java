package nullengine.world.storage;

import nullengine.world.WorldCommon;

import java.nio.file.Path;

public class WorldCommonLoader implements WorldLoader<WorldCommon> {

    private Path storage;

    public WorldCommonLoader(Path storage) {
        this.storage = storage;

    }

    @Override
    public void saveWorld() {

    }

    @Override
    public Path getStoragePath() {
        return storage;
    }

    @Override
    public WorldCommon loadWorld() {
        //TODO load world
        return null;
    }

    @Override
    public void flush() {

    }
}
