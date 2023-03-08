package engine.world.chunk;

import com.google.common.collect.Sets;
import engine.event.world.chunk.ChunkLoadEvent;
import engine.event.world.chunk.ChunkUnloadEvent;
import engine.logic.Tickable;
import engine.math.Math2;
import engine.math.SphereIterator;
import engine.player.Player;
import engine.world.WorldCommonDebug;
import engine.world.gen.ChunkGenExecutor;
import engine.world.gen.ChunkGenerator;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.apache.commons.lang3.Validate;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Collection;
import java.util.Optional;

public class DebugChunkManager implements ChunkManager, Tickable {

    private final WorldCommonDebug world;
    private final ChunkGenerator generator;

    private final Long2ObjectMap<Chunk> chunkMap;
    private final Long2ObjectMap<ChunkColumn> chunkColumnMap;

    private int viewDistance;
    private int viewDistanceSquared;

    public DebugChunkManager(WorldCommonDebug world, ChunkGenerator generator) {
        this.world = world;
        this.chunkMap = new Long2ObjectAVLTreeMap<>();
        this.chunkColumnMap = new Long2ObjectAVLTreeMap<>();
        this.generator = generator;
        setViewDistance(6);
        ChunkGenExecutor.start();
    }

    //    @Override
//    public boolean shouldChunkUnload(Chunk chunk, Player player) {
//        int viewDistanceSquared = 36864; //TODO game config
//        return !world.getCriticalChunks().contains(Chunk.getChunkIndex(chunk.getX(), chunk.getY(), chunk.getZ()))
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
        long chunkIndex = Chunk.index(x, y, z);
        return Optional.ofNullable(chunkMap.get(chunkIndex));
    }

    public ChunkColumn getChunkColumn(int x, int z) {
        return chunkColumnMap.get(Chunk.index(x, 0, z));
    }

    @Override
    public Chunk getOrLoadChunk(int x, int y, int z) {
        long index = Chunk.index(x, y, z);
        return chunkMap.computeIfAbsent(index, key -> loadChunk(index, x, y, z));
    }

    private boolean shouldChunkOnline(int x, int y, int z, Vector3ic pos) {
        return pos.distanceSquared(x, 0, z) <= viewDistanceSquared;
    }

    private synchronized Chunk loadChunk(long index, int x, int y, int z) {
        if (y < 0) { //Not buildable below 0
            return new AirChunk(world, x, y, z);
        }
//        if (!shouldChunkOnline(x, y, z, new Vector3i(0, 0, 0))) {
//            Chunk chunk = new AirChunk(world, x, y, z);
//            chunkMap.put(index, chunk);
//            return chunk;
//        }

        Chunk chunk;
        //Chunk has not been created
        chunk = new CubicChunk(world, x, y, z);
        chunkColumnMap.computeIfAbsent(Chunk.index(x, 0, z), key -> new ChunkColumn(world, x, z));
        generator.generateAsync(chunk).thenRun(() -> {
            chunkMap.put(index, chunk);
            world.getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        }).join();

//        chunkMap.put(index, chunk);
//        world.getGame().getEventBus().post(new ChunkLoadEvent(chunk));
        return chunk;
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        long index = Chunk.index(chunk.getX(), chunk.getY(), chunk.getZ());
        if (!chunkMap.containsKey(index))
            return;
        unloadChunk(index, chunk);
        chunkMap.remove(index);
    }

    private synchronized void unloadChunk(long index, Chunk chunk) {
        Validate.notNull(chunk);
//        chunkStorage.save(chunk);
        world.getGame().getEventBus().post(new ChunkUnloadEvent(chunk));
    }

    @Override
    public void unloadAll() {
        chunkMap.forEach(this::unloadChunk);
        chunkMap.clear();
        ChunkGenExecutor.stop();
//        chunkStorage.close();
    }

    @Override
    public void saveAll() {
//        chunkMap.values().forEach(chunkStorage::save);
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
        var position = Chunk.fromWorldPos(player.getControlledEntity().getPosition());
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
        int dx = 0;
        for (int i = 0; -viewDistance <= (dx += Math2.alternativeSignNaturalNumber(i)) && dx <= viewDistance; i++) {
            var zBoundSquared = viewDistanceSquared - dx * dx;
            var zBound = (int) Math.sqrt(zBoundSquared);
            int dz = 0;
            for (int k = 0; -zBound <= (dz += Math2.alternativeSignNaturalNumber(k)) && dz <= zBound; k++) {
                var yBoundSquared = zBoundSquared - dz * dz;
                var yBound = (int) Math.sqrt(yBoundSquared);
                int dy = 0;
                for (int j = 0; -yBound <= (dy += Math2.alternativeSignNaturalNumber(j)) && dy <= yBound; j++) {
                    if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) <= 1) {
                        // This chunk has already sent at the first step
                        continue;
                    }
                    this.sendChunkData(player, x + dx, y + dy, z + dz);
                }
            }
        }
    }

    public void handlePlayerMove(Player player, Vector3dc prevPos) {
        if (!player.isControllingEntity()) return; // We cannot do anything if the player does not control an entity
        var chunkPos = Chunk.fromWorldPos(player.getControlledEntity().getPosition());
        var prevChunkPos = Chunk.fromWorldPos(prevPos);
        if (chunkPos.equals(prevChunkPos)) return;
        var newArea = Sets.newHashSet(SphereIterator.getCoordinatesWithinSphere(viewDistance, chunkPos));
        var oldArea = Sets.newHashSet(SphereIterator.getCoordinatesWithinSphere(viewDistance, prevChunkPos));
        // The following terms "unload" and "load" is in client's view but not server's view
        for (Vector3i shouldUnload : Sets.difference(oldArea, newArea)) {
            sendUnloadNotice(player, shouldUnload.x, shouldUnload.y, shouldUnload.z);
        }
        for (Vector3i shouldLoad : Sets.difference(newArea, oldArea)) {
            sendChunkData(player, shouldLoad.x, shouldLoad.y, shouldLoad.z);
        }
    }

    private void sendUnloadNotice(Player player, int x, int y, int z) {
        getChunk(x, y, z).ifPresent(this::unloadChunk);
//        player.getNetworkHandler().sendPacket(new PacketUnloadChunk(world.getName(), x, y, z));
    }

    private void sendChunkData(Player player, int x, int y, int z) {
        var chunk = getOrLoadChunk(x, y, z);
//        if (chunk instanceof CubicChunk)
//            player.getNetworkHandler().sendPacket(new PacketChunkData(((CubicChunk) chunk)));
//        getChunk(x, y, z).filter(chunk -> chunk instanceof CubicChunk)
//                .ifPresent(chunk -> player.getNetworkHandler().sendPacket(new PacketChunkData((CubicChunk) chunk)));
    }

    @Override
    public void tick() {

    }
}
