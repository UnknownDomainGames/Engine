package engine.client.world;

import engine.block.Block;
import engine.client.world.chunk.WorldClientChunkManager;
import engine.component.Component;
import engine.entity.Entity;
import engine.event.block.cause.BlockChangeCause;
import engine.game.Game;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.server.network.NetworkHandler;
import engine.world.World;
import engine.world.WorldCreationSetting;
import engine.world.WorldProvider;
import engine.world.WorldSetting;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkConstants;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import org.joml.AABBd;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class WorldClient implements World, Runnable {

    private final Game game;
    private final WorldProvider worldProvider;
    private WorldClientChunkManager chunkManager;
    private long gameTick;

    protected boolean isClient = true;

    public WorldClient(Game game, NetworkHandler handler, WorldProvider provider) {
        this.game = game;
        this.worldProvider = provider;
        this.chunkManager = new WorldClientChunkManager(this);
    }

    @Override
    public void run() {

    }

    protected void tick() {

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
    public String getName() {
        return null;
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
    public void doDestroyEntity(Entity entity) {

    }

    @Override
    public long getGameTick() {
        return gameTick;
    }

    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance) {
        return null;
    }

    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore) {
        return null;
    }

    @Override
    public HitResult raycast(Vector3fc from, Vector3fc dir, float distance) {
        return null;
    }

    @Override
    public List<Entity> getEntities() {
        return null;
    }

    @Override
    public List<Entity> getEntities(Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithType(Class<T> entityType) {
        return null;
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(AABBd boundingBox) {
        return null;
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return null;
    }

    @Override
    public List<Entity> getEntitiesWithSphere(double centerX, double centerY, double centerZ, double radius) {
        return null;
    }

    @Override
    public EntityHitResult raycastEntity(Vector3fc from, Vector3fc dir, float distance) {
        return null;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkManager.loadChunk(chunkX, chunkY, chunkZ);
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkManager.getLoadedChunks();
    }

    @Override
    public void unload() {

    }

    @Override
    public boolean isUnloaded() {
        return false;
    }

    @Override
    public void save() {

    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z) {
        Chunk chunk = chunkManager.loadChunk(x >> ChunkConstants.CHUNK_X_BITS, y >> ChunkConstants.CHUNK_Y_BITS, z >> ChunkConstants.CHUNK_Z_BITS);
        return chunk != null ? chunk.getBlock(x, y, z) : Registries.getBlockRegistry().air();
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlock(x, y, z).getId();
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return getBlock(x, y, z) == Registries.getBlockRegistry().air();
    }

    @Nonnull
    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Nonnull
    @Override
    public Block destroyBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return Optional.empty();
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return false;
    }

    @Override
    public <C extends Component> World setComponent(@Nonnull Class<C> type, @Nullable C value) {
        return null;
    }

    @Override
    public <C extends Component> World removeComponent(@Nonnull Class<C> type) {
        return null;
    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return null;
    }
}
