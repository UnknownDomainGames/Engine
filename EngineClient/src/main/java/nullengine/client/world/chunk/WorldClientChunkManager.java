package nullengine.client.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.client.world.WorldClient;
import nullengine.event.world.chunk.ChunkUnloadEvent;
import nullengine.player.Player;
import nullengine.server.network.packet.PacketChunkData;
import nullengine.world.chunk.BlankChunk;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.ChunkManager;
import nullengine.world.chunk.storage.ChunkStorer;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class WorldClientChunkManager implements ChunkManager<WorldClient> {

    private final WeakReference<WorldClient> world;
    private final LongObjectMap<Chunk> chunkMap;
    private final Chunk blank;

    public WorldClientChunkManager(WorldClient world) {
        this.world = new WeakReference<>(world);
        this.chunkMap = new LongObjectHashMap<>();
        blank = new BlankChunk(world,0,0,0);
    }

    @Override
    public boolean shouldChunkUnload(Chunk chunk, Player player) {
        return false;
    }

    @Override
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
    public void unloadChunk(int x, int y, int z) {
        long chunkIndex = ChunkConstants.getChunkIndex(x, y, z);
        if(chunkMap.containsKey(chunkIndex)){
            var chunk = chunkMap.remove(chunkIndex);
            world.get().getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
        }
    }

    @Override
    public WorldClient getWorld() {
        return world.get();
    }

    @Override
    public Collection<Chunk> getChunks() {
        return chunkMap.values();
    }
}
