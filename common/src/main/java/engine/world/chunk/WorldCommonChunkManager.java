package engine.world.chunk;

import engine.event.world.chunk.ChunkLoadEvent;
import engine.event.world.chunk.ChunkUnloadEvent;
import engine.logic.Tickable;
import engine.math.Math2;
import engine.player.Player;
import engine.server.network.packet.PacketChunkData;
import engine.world.WorldCommon;
import engine.world.chunk.storage.RegionBasedChunkStorage;
import engine.world.gen.ChunkGenerator;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Optional;

import static engine.world.chunk.ChunkConstants.getChunkIndex;

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

    private boolean shouldChunkOnline(int x, int y, int z, ChunkPos pos) {
        return pos.distanceSquared(x, 0, z) <= viewDistanceSquared;
    }

    private synchronized Chunk loadChunk(long index, int x, int y, int z) {
        if (!shouldChunkOnline(x, y, z, ChunkPos.of(0, 0, 0))) {
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
        chunkStorage.close();
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

    public void handlePlayerJoin(Player player) {
        if (!player.isControllingEntity()) return; // We cannot do anything if the player does not control an entity
        var position = ChunkPos.fromWorldPos(player.getControlledEntity().getPosition());
        var x = position.x();
        var y = position.y();
        var z = position.z();

        // First: send the nearby 27 chunks to the client
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    this.sendChunkData(player, x + i, y + j, z + k);
                }
            }
        }

        // Then: send all the remaining chunks
        int dx;
        for (int i = 0; -viewDistance <= (dx = Math2.alternativeSignNaturalNumber(i)) && dx <= viewDistance; i++) {
            var zBoundSquared = viewDistanceSquared - dx * dx;
            var zBound = (int) Math.sqrt(zBoundSquared);
            int dz;
            for (int k = 0; -zBound <= (dz = Math2.alternativeSignNaturalNumber(k)) && dz <= zBound; k++) {
                var yBoundSquared = zBoundSquared - dz * dz;
                var yBound = (int) Math.sqrt(yBoundSquared);
                int dy;
                for (int j = 0; -yBound <= (dy = Math2.alternativeSignNaturalNumber(j)) && dy <= yBound; j++) {
                    if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) <= 1) {
                        // This chunk has already sent at the first step
                        continue;
                    }
                    this.sendChunkData(player, dx, dy, dz);
                }
            }
        }
    }

    private void sendChunkData(Player player, int x, int y, int z) {
        getChunk(x, y, z).filter(chunk -> chunk instanceof CubicChunk)
                .ifPresent(chunk -> player.getNetworkHandler().sendPacket(new PacketChunkData((CubicChunk) chunk)));
    }

    @Override
    public void tick() {

    }
}
