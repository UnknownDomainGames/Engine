package unknowndomain.engine.world.storage;

import unknowndomain.engine.world.WorldCommon;
import unknowndomain.engine.world.chunk.WorldCommonChunkStorer;
import unknowndomain.engine.world.chunk.storage.ChunkStorer;

import java.nio.file.Path;

public class WorldCommonLoader implements WorldLoader<WorldCommon> {

    private Path storage;
    
    public WorldCommonLoader(Path storage){
        this.storage = storage;

    }

    @Override
    public void saveWorld() {

    }

    @Override
    public Path getStorageLocation() {
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
