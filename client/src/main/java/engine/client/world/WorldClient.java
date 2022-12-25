package engine.client.world;

import engine.block.AirBlock;
import engine.block.Block;
import engine.block.component.DestroyBehavior;
import engine.block.component.NeighborChangeListener;
import engine.block.component.PlaceBehavior;
import engine.block.state.BlockState;
import engine.client.world.chunk.WorldClientChunkManager;
import engine.component.Component;
import engine.component.ComponentAgent;
import engine.entity.Entity;
import engine.event.block.BlockChangeEvent;
import engine.event.block.BlockDestroyEvent;
import engine.event.block.BlockPlaceEvent;
import engine.event.block.BlockReplaceEvent;
import engine.event.block.cause.BlockChangeCause;
import engine.game.Game;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.util.Direction;
import engine.world.*;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkConstants;
import engine.world.collision.DefaultCollisionManager;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.primitives.AABBd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static engine.world.chunk.ChunkConstants.*;

public class WorldClient implements World, Runnable {

    private final Game game;
    private final WorldProvider worldProvider;
    private String name;
    private WorldClientChunkManager chunkManager;
    private final DefaultEntityManager entityManager;
    private final CollisionManager collisionManager;
    private final ComponentAgent componentAgent = new ComponentAgent();
    private long gameTick;

    public WorldClient(Game game, WorldProvider provider, String name) {
        this.game = game;
        this.worldProvider = provider;
        this.name = name;
        this.chunkManager = new WorldClientChunkManager(this);
        this.collisionManager = new DefaultCollisionManager(this);
        this.entityManager = new DefaultEntityManager(this);
    }

    @Override
    public void run() {

    }

    public void tick() {
        tickEntityMotion();
        chunkManager.tick();
        entityManager.tick();
        gameTick++;
    }

    protected void tickEntityMotion() {
        for (Entity entity : this.getEntities()) {
            Vector3d position = entity.getPosition();
            Vector3f motion = entity.getMotion();
            position.add(motion);
        }
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
        return name;
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
        return entityManager.spawnEntity(entityType, x, y, z);
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position) {
        return entityManager.spawnEntity(entityType, position);
    }

    @Override
    public Entity spawnEntity(String providerName, double x, double y, double z) {
        return entityManager.spawnEntity(providerName, x, y, z);
    }

    @Override
    public Entity spawnEntity(String provider, Vector3dc position) {
        return entityManager.spawnEntity(provider, position);
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
        return collisionManager.raycastBlock(from, dir, distance);
    }

    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore) {
        return collisionManager.raycastBlock(from, dir, distance, ignore);
    }

    @Override
    public HitResult raycast(Vector3fc from, Vector3fc dir, float distance) {
        BlockHitResult blockHitResult = raycastBlock(from, dir, distance);

        float newDistance;
        if (blockHitResult.isSuccess()) {
            BlockPos pos = blockHitResult.getPos();
            newDistance = from.distance(new Vector3f(pos).add(blockHitResult.getHitPoint()));
        } else {
            newDistance = distance;
        }

        EntityHitResult entityHitResult = raycastEntity(from, dir, newDistance);
        if (entityHitResult.isSuccess()) {
            return entityHitResult;
        }

        return blockHitResult;
    }

    @Override
    public List<Entity> getEntities() {
        return entityManager.getEntities();
    }

    @Override
    public List<Entity> getEntities(Predicate<Entity> predicate) {
        return entityManager.getEntities(predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithType(Class<T> entityType) {
        return entityManager.getEntitiesWithType(entityType);
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(AABBd boundingBox) {
        return entityManager.getEntitiesWithBoundingBox(boundingBox);
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return entityManager.getEntitiesWithBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public List<Entity> getEntitiesWithSphere(double centerX, double centerY, double centerZ, double radius) {
        return entityManager.getEntitiesWithSphere(centerX, centerY, centerZ, radius);
    }

    @Override
    public EntityHitResult raycastEntity(Vector3fc from, Vector3fc dir, float distance) {
        return entityManager.raycastEntity(from, dir, distance);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ, boolean shouldLoadWhenNonexist) {
        return shouldLoadWhenNonexist ? chunkManager.getOrLoadChunk(chunkX, chunkY, chunkZ) : chunkManager.getChunk(chunkX, chunkY, chunkZ).orElse(null);
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
    public BlockState getBlock(int x, int y, int z) {
        Chunk chunk = this.getChunk(x >> ChunkConstants.CHUNK_X_BITS, y >> ChunkConstants.CHUNK_Y_BITS, z >> ChunkConstants.CHUNK_Z_BITS, false);
        return chunk != null ? chunk.getBlock(x, y, z) : Registries.getBlockRegistry().air().getDefaultState();
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return getBlock(x, y, z).getPrototype() == Registries.getBlockRegistry().air();
    }

    //TODO: check if this implementation shall modify or not
    @Nonnull
    @Override
    public BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause, boolean shouldNotify) {
        BlockState oldBlock = getBlock(pos);
        BlockChangeEvent pre, post;
        if (block.getPrototype() == AirBlock.AIR) {
            pre = new BlockDestroyEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockDestroyEvent.Post(this, pos, oldBlock, block, cause);
        } else if (oldBlock == AirBlock.AIR.getDefaultState()) {
            pre = new BlockPlaceEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockPlaceEvent.Post(this, pos, oldBlock, block, cause);
        } else {
            pre = new BlockReplaceEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockReplaceEvent.Post(this, pos, oldBlock, block, cause);
        }
        if (!getGame().getEventBus().post(pre)) {
            getChunk(pos.x() >> CHUNK_X_BITS, pos.y() >> CHUNK_Y_BITS, pos.z() >> CHUNK_Z_BITS, true)
                    .setBlock(pos, block, cause);

            oldBlock.getPrototype().getComponent(DestroyBehavior.class).ifPresent(destroyBehavior -> destroyBehavior.onDestroyed(this, pos, oldBlock, cause));
            block.getPrototype().getComponent(PlaceBehavior.class).ifPresent(placeBehavior -> placeBehavior.onPlaced(this, pos, block, cause));

            getGame().getEventBus().post(post);
            if (shouldNotify) {
                notifyNeighborChanged(pos, block, cause);
            }
        }
        return oldBlock;
    }

    protected void notifyNeighborChanged(BlockPos pos, BlockState block, BlockChangeCause cause) {
        for (Direction direction : Direction.VALUES) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighbor = getBlock(neighborPos);
            neighbor.getPrototype().getComponent(NeighborChangeListener.class)
                    .ifPresent(listener -> listener.onNeighborChanged(this, neighborPos, neighbor, direction.opposite(), pos, block, cause));
        }
    }

    @Nonnull
    @Override
    public BlockState destroyBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return componentAgent.getComponent(type);
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return componentAgent.hasComponent(type);
    }

    @Override
    public <C extends Component> World setComponent(@Nonnull Class<C> type, @Nullable C value) {
        this.componentAgent.setComponent(type, value);
        return this;
    }

    @Override
    public <C extends Component> World removeComponent(@Nonnull Class<C> type) {
        this.componentAgent.removeComponent(type);
        return this;
    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return componentAgent.getComponents();
    }

    public WorldClientChunkManager getChunkManager() {
        return chunkManager;
    }

    @Override
    public boolean isLogicSide() {
        return false;
    }
}
