package engine.client.world.chunk;

import engine.client.world.WorldClient;
import engine.event.world.chunk.ChunkLoadEvent;
import engine.event.world.chunk.ChunkUnloadEvent;
import engine.player.Player;
import engine.server.network.packet.s2c.PacketChunkData;
import engine.world.chunk.AirChunk;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkManager;
import engine.world.chunk.CubicChunk;
import engine.world.gen.ChunkGenerator;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.apache.commons.lang3.Validate;
import org.joml.Vector3dc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class WorldClientChunkManager implements ChunkManager {

    private final WorldClient world;
    private final LongObjectMap<Chunk> chunkMap;
    private final Chunk blank;

    public WorldClientChunkManager(WorldClient world) {
        this.world = world;
        this.chunkMap = new LongObjectHashMap<>();
        blank = new AirChunk(world, 0, 0, 0);
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        long chunkIndex = Chunk.index(x, y, z);
        return Optional.ofNullable(chunkMap.get(chunkIndex));
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        long index = Chunk.index(x, y, z);
        return chunkMap.getOrDefault(index, blank);
    }

    public Chunk loadChunk(int x, int y, int z) {
        long chunkIndex = Chunk.index(x, y, z);
        if (!chunkMap.containsKey(chunkIndex)) {
            return blank;
        }
        return chunkMap.get(chunkIndex);
    }

    public Chunk loadChunkFromPacket(PacketChunkData packet) {
        long index = Chunk.index(packet.getChunkX(), packet.getChunkY(), packet.getChunkZ());
        var chunk = new CubicChunk(world, packet.getChunkX(), packet.getChunkY(), packet.getChunkZ());
        try (var stream = new ByteArrayInputStream(packet.getRawData())) {
            chunk.readBlockContent(new DataInputStream(stream));
        } catch (IOException e) {

        }
        chunkMap.put(index, chunk);
        world.getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        return chunk;
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        throw new UnsupportedOperationException("Client-side have no generator");
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        long index = Chunk.index(chunk);
        if (!chunkMap.containsKey(index))
            return;
        unloadChunk(index, chunk);
        chunkMap.remove(index);
    }

    private synchronized void unloadChunk(long index, Chunk chunk) {
        Validate.notNull(chunk);
        world.getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
    }

    @Override
    public void unloadAll() {
        chunkMap.forEach(this::unloadChunk);
        chunkMap.clear();
    }

    @Override
    public void saveAll() {

    }

    public void tick() {

    }

    @Override
    public void handlePlayerJoin(Player player) {

    }

    @Override
    public void handlePlayerMove(Player player, Vector3dc prevPos) {

    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkMap.values();
    }
}
