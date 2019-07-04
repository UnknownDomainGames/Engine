package nullengine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.event.Listener;
import nullengine.event.world.WorldTickEvent;
import nullengine.event.world.chunk.ChunkLoadEvent;
import nullengine.event.world.chunk.ChunkUnloadEvent;
import nullengine.player.Player;
import nullengine.world.WorldCommon;
import nullengine.world.chunk.storage.ChunkStorer;
import nullengine.world.gen.ChunkGenerator;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class WorldCommonChunkManager implements ChunkManager<WorldCommon> {
    private final WeakReference<WorldCommon> world;
    private final ChunkStorer chunkLoader;
    private final LongObjectMap<Chunk> chunkMap;
    private final ChunkGenerator generator;

    public WorldCommonChunkManager(WorldCommon world, ChunkGenerator generator) {
        this.world = new WeakReference<>(world);
        this.chunkLoader = new WorldCommonChunkStorer(this);
        this.chunkMap = new LongObjectHashMap<>();
        this.generator = generator;
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        int viewDistanceSquared = 36864; //TODO game config
        return !world.get().getCriticalChunks().contains(ChunkConstants.getChunkIndex(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()))
                && player.getControlledEntity().getPosition().distanceSquared(new Vector3f(chunk.getMin().add(chunk.getMax(), new Vector3f()).div(2))) > viewDistanceSquared;
    }


    private boolean shouldChunkOnline(int x, int y, int z, Vector3d pos) {
        int viewDistanceSquared = 36864; //TODO game config
        return world.get().getCriticalChunks().contains(ChunkConstants.getChunkIndex(x, y, z))
                || pos.distanceSquared(new Vector3d((x << ChunkConstants.BITS_X) + ChunkConstants.SIZE_X / 2, (y << ChunkConstants.BITS_Y) + ChunkConstants.SIZE_Y / 2, (z << ChunkConstants.BITS_Z) + ChunkConstants.SIZE_Z / 2)) <= viewDistanceSquared;
    }

    @Override
    public Chunk loadChunk(int x, int y, int z) {
        if (!shouldChunkOnline(x, y, z, new Vector3d(0, 5, 0)))
            return new BlankChunk(world.get(), x, y, z);
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if (chunkMap.containsKey(chunkIndex)) {
            return chunkMap.get(chunkIndex);
        }
        Chunk chunk = chunkLoader.load(x, y, z);
        if (chunk == null) { //Chunk has not been created
            chunk = new ChunkImpl(world.get(), x, y, z);
            generator.base(chunk); //TODO: not directly call this thing
        }
        chunkMap.put(chunkIndex, chunk);
        world.get().getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        return chunk;
    }

    @Override
    public void unloadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if (chunkMap.containsKey(chunkIndex)) {
            Chunk chunk = chunkMap.get(chunkIndex);
            chunkLoader.save(chunk);
            chunkMap.remove(chunkIndex);
            world.get().getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
        }
    }

    @Listener
    public void tickWorld(WorldTickEvent event) {
//        this.getChunks().forEach(this::tickChunk);
    }

    @Override
    public WorldCommon getWorld() {
        return world.get();
    }

    @Override
    public Collection<Chunk> getChunks() {
        return chunkMap.values();
    }

    public ChunkGenerator getChunkGenerator() {
        return generator;
    }
}
