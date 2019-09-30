package nullengine.client.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.client.world.WorldClient;
import nullengine.event.world.chunk.ChunkUnloadEvent;
import nullengine.player.Player;
import nullengine.world.chunk.AirChunk;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.ChunkManager;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Optional;

public class WorldClientChunkManager implements ChunkManager {

    private final WeakReference<WorldClient> world;
    private final LongObjectMap<Chunk> chunkMap;
    private final Chunk blank;

    public WorldClientChunkManager(WorldClient world) {
        this.world = new WeakReference<>(world);
        this.chunkMap = new LongObjectHashMap<>();
        blank = new AirChunk(world, 0, 0, 0);
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        return false;
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        return Optional.empty();
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        return null;
    }

    public Chunk loadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if(!chunkMap.containsKey(chunkIndex)){
            return blank;
        }
        return chunkMap.get(chunkIndex);
    }

//    public Chunk loadChunkFromPacket(PacketChunkData packet){
//        long chunkIndex = ChunkConstants.getChunkIndex(packet.getChunkX(), packet.getChunkY(), packet.getChunkZ());
//
//    }

    @Override
    public void unloadChunk(Chunk chunk) {
        long index = ChunkConstants.getChunkIndex(chunk);
        if (chunkMap.containsKey(index)) {
            chunkMap.remove(index);
            world.get().getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
        }
    }

    @Override
    public void unloadAll() {

    }

    @Override
    public void saveAll() {

    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkMap.values();
    }
}
