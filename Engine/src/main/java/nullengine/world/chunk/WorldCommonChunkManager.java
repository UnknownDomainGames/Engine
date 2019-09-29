package nullengine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.event.world.chunk.ChunkLoadEvent;
import nullengine.event.world.chunk.ChunkUnloadEvent;
import nullengine.logic.Tickable;
import nullengine.player.Player;
import nullengine.world.WorldCommon;
import nullengine.world.chunk.storage.RegionBasedChunkStorage;
import nullengine.world.gen.ChunkGenerator;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class WorldCommonChunkManager implements ChunkManager, Tickable {

    private final WeakReference<WorldCommon> world;
    private final ChunkStorage chunkStorage;
    private final ChunkGenerator generator;

    private final LongObjectMap<Chunk> chunkMap;

    public WorldCommonChunkManager(WorldCommon world, ChunkGenerator generator) {
        this.world = new WeakReference<>(world);
        this.chunkStorage = new RegionBasedChunkStorage(world, world.getStoragePath().resolve("chunk"));
        this.chunkMap = new LongObjectHashMap<>();
        this.generator = generator;
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        int viewDistanceSquared = 36864; //TODO game config
        return !world.get().getCriticalChunks().contains(ChunkConstants.getChunkIndex(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()))
                && player.getControlledEntity().getPosition().distanceSquared(new Vector3d(chunk.getMin().add(chunk.getMax(), new Vector3f()).div(2))) > viewDistanceSquared;
    }

    @Override
    public Chunk getChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        return chunkMap.get(chunkIndex);
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        return chunkMap.computeIfAbsent(chunkIndex, key -> loadChunk(x, y, z));
    }


    private boolean shouldChunkOnline(int x, int y, int z, Vector3d pos) {
        int viewDistanceSquared = 36864; //TODO game config
        return world.get().getCriticalChunks().contains(ChunkConstants.getChunkIndex(x, y, z))
                || pos.distanceSquared(new Vector3d((x << ChunkConstants.BITS_X) + ChunkConstants.SIZE_X / 2, (y << ChunkConstants.BITS_Y) + ChunkConstants.SIZE_Y / 2, (z << ChunkConstants.BITS_Z) + ChunkConstants.SIZE_Z / 2)) <= viewDistanceSquared;
    }

    @Override
    public synchronized Chunk loadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if (!shouldChunkOnline(x, y, z, new Vector3d(0, 5, 0))) {
            Chunk chunk = new AirChunk(world.get(), x, y, z);
            chunkMap.put(chunkIndex, chunk);
            return chunk;
        }

        if (chunkMap.containsKey(chunkIndex)) {
            return chunkMap.get(chunkIndex);
        }
        Chunk chunk = chunkStorage.load(x, y, z);
        if (chunk == null) { //Chunk has not been created
            chunk = new CubicChunk(world.get(), x, y, z);
            generator.generate(chunk);
        }
        chunkMap.put(chunkIndex, chunk);
        world.get().getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        return chunk;
    }

    @Override
    public synchronized void unloadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if (chunkMap.containsKey(chunkIndex)) {
            Chunk chunk = chunkMap.get(chunkIndex);
            chunkStorage.save(chunk);
            chunkMap.remove(chunkIndex);
            world.get().getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
        }
    }

    @Override
    public synchronized void unloadChunk(Chunk chunk) {
        if (chunk == null) {
            return;
        }
        long chunkIndex = ChunkConstants.getChunkIndex(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ());
        if (!chunkMap.containsKey(chunkIndex))
            return;
        chunkStorage.save(chunk);
        chunkMap.remove(chunkIndex);
        world.get().getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
    }

    @Override
    public void unloadAll() {
        chunkMap.values().forEach(this::unloadChunk);
    }

    @Override
    public void saveAll() {
        chunkMap.values().forEach(chunkStorage::save);
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkMap.values();
    }

    public ChunkGenerator getChunkGenerator() {
        return generator;
    }

    @Override
    public void tick() {

    }
}
