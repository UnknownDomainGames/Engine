package unknowndomain.engine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.WorldCommon;
import unknowndomain.engine.world.chunk.storage.ChunkStorer;
import unknowndomain.engine.world.gen.ChunkGenerator;

import java.lang.ref.WeakReference;

public class WorldCommonChunkManager implements ChunkManager<WorldCommon> {

    private final WeakReference<WorldCommon> world;
    private final ChunkStorer chunkLoader;
    private final LongObjectMap<Chunk> chunkMap;
    private final ChunkGenerator generator;

    public WorldCommonChunkManager(WorldCommon world, ChunkGenerator generator){
        this.world = new WeakReference<>(world);
        this.chunkLoader = new WorldCommonChunkStorer(this);
        this.chunkMap = new LongObjectHashMap<>();
        this.generator = generator;
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        int viewDistanceSquared = 144; //TODO game config
        return !world.get().getCriticalChunks().contains(ChunkConstants.getChunkIndex(chunk.getChunkX(),chunk.getChunkY(),chunk.getChunkZ()))
                && player.getControlledEntity().getPosition().distanceSquared(new Vector3d(chunk.getMin().add(chunk.getMax(), new Vector3f()).div(2))) > viewDistanceSquared;
    }

    @Override
    public Chunk loadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if(chunkMap.containsKey(chunkIndex)){
            return chunkMap.get(chunkIndex);
        }
        Chunk chunk = chunkLoader.load(x, y, z);
        chunkMap.put(chunkIndex,chunk);
        return chunk;
    }

    @Override
    public void unloadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if(chunkMap.containsKey(chunkIndex)) {
            chunkLoader.save(chunkMap.get(chunkIndex));
        }
        chunkMap.remove(chunkIndex);
    }

    @Override
    public WorldCommon getWorld() {
        return world.get();
    }

    public ChunkGenerator getChunkGenerator() {
        return generator;
    }
}
