package unknowndomain.engine.world.chunk;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.storage.ChunkStorer;

import java.lang.ref.WeakReference;

public class WorldCommonChunkStorer implements ChunkStorer {

    private final WeakReference<WorldCommonChunkManager> chunkManager;

    public WorldCommonChunkStorer(WorldCommonChunkManager loader){
        chunkManager = new WeakReference<>(loader);
    }

    @Override
    public World getWorld() {
        return chunkManager.get().getWorld();
    }

    @Override
    public Chunk load(int chunkX, int chunkY, int chunkZ) {
        return null;
    }

    @Override
    public void save(Chunk chunk) {

    }
}
