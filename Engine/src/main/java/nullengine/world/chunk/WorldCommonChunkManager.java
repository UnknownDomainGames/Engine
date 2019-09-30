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

import java.util.Collection;
import java.util.Optional;

public class WorldCommonChunkManager implements ChunkManager, Tickable {

    private final WorldCommon world;
    private final ChunkStorage chunkStorage;
    private final ChunkGenerator generator;

    private final LongObjectMap<Chunk> chunkMap;

    public WorldCommonChunkManager(WorldCommon world, ChunkGenerator generator) {
        this.world = world;
        this.chunkStorage = new RegionBasedChunkStorage(world, world.getStoragePath().resolve("chunk"));
        this.chunkMap = new LongObjectHashMap<>();
        this.generator = generator;
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        int viewDistanceSquared = 36864; //TODO game config
        return !world.getCriticalChunks().contains(ChunkConstants.getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ()))
                && player.getControlledEntity().getPosition().distanceSquared(new Vector3d(chunk.getMin().add(chunk.getMax(), new Vector3f()).div(2))) > viewDistanceSquared;
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        return Optional.ofNullable(chunkMap.get(chunkIndex));
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        long index = ChunkConstants.getChunkIndex(x, y, z);
        return chunkMap.computeIfAbsent(index, key -> loadChunk(index, x, y, z));
    }


    private boolean shouldChunkOnline(int x, int y, int z, Vector3d pos) {
        int viewDistanceSquared = 36864; //TODO game config
        return world.getCriticalChunks().contains(ChunkConstants.getChunkIndex(x, y, z))
                || pos.distanceSquared(new Vector3d((x << ChunkConstants.BITS_X) + ChunkConstants.SIZE_X / 2, (y << ChunkConstants.BITS_Y) + ChunkConstants.SIZE_Y / 2, (z << ChunkConstants.BITS_Z) + ChunkConstants.SIZE_Z / 2)) <= viewDistanceSquared;
    }

    private synchronized Chunk loadChunk(long index, int x, int y, int z) {
        if (chunkMap.containsKey(index)) {
            return chunkMap.get(index);
        }

        if (!shouldChunkOnline(x, y, z, new Vector3d(0, 5, 0))) {
            Chunk chunk = new AirChunk(world, x, y, z);
            chunkMap.put(index, chunk);
            return chunk;
        }

        Chunk chunk = chunkStorage.load(x, y, z);
        if (chunk == null) { //Chunk has not been created
            chunk = new CubicChunk(world, x, y, z);
            generator.generate(chunk);
        }
        chunkMap.put(index, chunk);
        world.getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        return chunk;
    }

    @Override
    public synchronized void unloadChunk(Chunk chunk) {
        if (chunk == null) {
            return;
        }
        long chunkIndex = ChunkConstants.getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ());
        if (!chunkMap.containsKey(chunkIndex))
            return;
        chunkStorage.save(chunk);
        chunkMap.remove(chunkIndex);
        world.getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
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
