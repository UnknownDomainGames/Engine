package nullengine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.event.world.chunk.ChunkLoadEvent;
import nullengine.event.world.chunk.ChunkUnloadEvent;
import nullengine.logic.Tickable;
import nullengine.world.WorldCommon;
import nullengine.world.chunk.storage.RegionBasedChunkStorage;
import nullengine.world.gen.ChunkGenerator;
import org.apache.commons.lang3.Validate;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Collection;
import java.util.Optional;

import static nullengine.world.chunk.ChunkConstants.*;

public class WorldCommonChunkManager implements ChunkManager, Tickable {

    private final WorldCommon world;
    private final ChunkStorage chunkStorage;
    private final ChunkGenerator generator;

    private final LongObjectMap<Chunk> chunkMap;

    private int viewDistance;
    private int viewDistanceSquared;

    public WorldCommonChunkManager(WorldCommon world, ChunkGenerator generator) {
        this.world = world;
        this.chunkStorage = new RegionBasedChunkStorage(world, world.getStoragePath().resolve("chunk"));
        this.chunkMap = new LongObjectHashMap<>();
        this.generator = generator;
        setViewDistance(12);
    }

//    @Override
//    public boolean shouldChunkUnload(Chunk chunk, Player player) {
//        int viewDistanceSquared = 36864; //TODO game config
//        return !world.getCriticalChunks().contains(ChunkConstants.getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ()))
//                && player.getControlledEntity().getPosition().distanceSquared(new Vector3d(chunk.getMin().add(chunk.getMax(), new Vector3f()).div(2))) > viewDistanceSquared;
//    }

    public int getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        this.viewDistanceSquared = viewDistance * viewDistance;
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        long chunkIndex = getChunkIndex(x, y, z);
        return Optional.ofNullable(chunkMap.get(chunkIndex));
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        long index = getChunkIndex(x, y, z);
        return chunkMap.computeIfAbsent(index, key -> loadChunk(index, x, y, z));
    }

    private boolean shouldChunkOnline(int x, int y, int z, Vector3dc pos) {
        return pos.distanceSquared(new Vector3d((x << BITS_X) + SIZE_X / 2, (y << BITS_Y) + SIZE_Y / 2, (z << BITS_Z) + SIZE_Z / 2)) <= viewDistanceSquared;
    }

    private synchronized Chunk loadChunk(long index, int x, int y, int z) {
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
    public void unloadChunk(Chunk chunk) {
        long index = getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ());
        if (!chunkMap.containsKey(index))
            return;
        unloadChunk(index, chunk);
        chunkMap.remove(index);
    }

    private synchronized void unloadChunk(long index, Chunk chunk) {
        Validate.notNull(chunk);
        chunkStorage.save(chunk);
        world.getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
    }

    @Override
    public void unloadAll() {
        chunkMap.forEach(this::unloadChunk);
        chunkMap.clear();
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
