package nullengine.client.world;

import nullengine.block.Block;
import nullengine.client.world.chunk.WorldClientChunkManager;
import nullengine.entity.Entity;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.server.network.NetworkHandler;
import nullengine.world.*;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.collision.WorldCollisionManager;
import org.joml.Vector3dc;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class WorldClient implements World, Runnable {

    private final NetworkHandler networkHandler;

    private final Game game;
    private final WorldProvider worldProvider;
    private WorldClientChunkManager chunkManager;
    private long gameTick;

    protected boolean isClient = true;

    public WorldClient(Game game, NetworkHandler handler, WorldProvider provider){
        this.game = game;
        this.networkHandler = handler;
        this.worldProvider = provider;
        this.chunkManager = new WorldClientChunkManager(this);
    }

    @Override
    public void run() {

    }

    protected void tick(){

        tickChunks();
        gameTick++;
    }

    protected void tickChunks() {
//        chunkManager.getChunks().forEach(this::tickChunk);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public WorldProvider getProvider() {
        return worldProvider;
    }

    @Override
    public Path getStoragePath() {
        return Path.of("");
    }

    @Override
    public WorldCreationSetting getCreationSetting() {
        return null;
    }

    @Override
    public WorldSetting getSetting() {
        return null;
    }

    @Override
    public WorldCollisionManager getCollisionManager() {
        return null;
    }

    @Override
    public WorldEntityManager getEntityManager() {
        return null;
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, double x, double y, double z) {
        return null;
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position) {
        return null;
    }

    @Override
    public Entity spawnEntity(String providerName, double x, double y, double z) {
        return null;
    }

    @Override
    public Entity spawnEntity(String provider, Vector3dc position) {
        return null;
    }

    @Override
    public long getGameTick() {
        return gameTick;
    }

    @Override
    public List<Entity> getEntities() {
        return null;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkManager.loadChunk(chunkX, chunkY, chunkZ);
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkManager.getChunks();
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z){
        Chunk chunk = chunkManager.loadChunk(x >> ChunkConstants.BITS_X, y >> ChunkConstants.BITS_Y, z >> ChunkConstants.BITS_Z);
        return chunk != null ? chunk.getBlock(x, y, z) : Registries.getBlockRegistry().air();
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlock(x, y, z).getId();
    }

    @Nonnull
    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Nonnull
    @Override
    public Block destoryBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }
}