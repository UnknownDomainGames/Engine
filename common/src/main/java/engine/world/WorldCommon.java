package engine.world;

import engine.block.AirBlock;
import engine.block.Block;
import engine.block.component.DestroyBehavior;
import engine.block.component.NeighborChangeListener;
import engine.block.component.PlaceBehavior;
import engine.block.state.BlockState;
import engine.component.Component;
import engine.component.ComponentAgent;
import engine.entity.Entity;
import engine.event.block.BlockChangeEvent;
import engine.event.block.BlockDestroyEvent;
import engine.event.block.BlockPlaceEvent;
import engine.event.block.BlockReplaceEvent;
import engine.event.block.cause.BlockChangeCause;
import engine.game.Game;
import engine.math.AABBs;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.util.Direction;
import engine.world.chunk.Chunk;
import engine.world.chunk.WorldCommonChunkManager;
import engine.world.collision.DefaultCollisionManager;
import engine.world.gen.ChunkGenerator;
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

public class WorldCommon implements World {

    private final Game game;
    private final WorldProvider provider;
    private final Path storagePath;
    private final String name;
    private final WorldCreationSetting creationSetting;

    private final ComponentAgent componentAgent = new ComponentAgent();

    private final PhysicsSystem physicsSystem = new PhysicsSystem(); // prepare for split
    private final CollisionManager collisionManager;
    private final DefaultEntityManager entityManager;

    private WorldCommonChunkManager chunkManager;

    //    private final Ticker ticker;
    private long gameTick;
//    private ExecutorService service;

    private boolean unloaded = false;

    public WorldCommon(Game game, WorldProvider provider, Path storagePath, String name, WorldCreationSetting creationSetting, ChunkGenerator chunkGenerator) {
        this.game = game;
        this.provider = provider;
        this.storagePath = storagePath;
        this.name = name;
        this.creationSetting = creationSetting;
        this.chunkManager = new WorldCommonChunkManager(this, chunkGenerator);
//        this.ticker = new Ticker(this::tick, Ticker.LOGIC_TICK); // TODO: make tps configurable
        this.collisionManager = new DefaultCollisionManager(this);
        this.entityManager = new DefaultEntityManager(this);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public WorldProvider getProvider() {
        return provider;
    }

    @Override
    public Path getStoragePath() {
        return storagePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WorldCreationSetting getCreationSetting() {
        return creationSetting;
    }

    @Override
    public WorldSetting getSetting() {
        return null;
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
        componentAgent.setComponent(type, value);
        return this;
    }

    @Override
    public <T extends Component> World removeComponent(@Nonnull Class<T> type) {
        componentAgent.removeComponent(type);
        return this;
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return componentAgent.getComponents();
    }

    @Override
    public List<Entity> getEntities() {
        return entityManager.getEntities();
    }

    public List<Entity> getEntities(Predicate<Entity> predicate) {
        return entityManager.getEntities(predicate);
    }

    public <T extends Entity> List<T> getEntitiesWithType(Class<T> entityType) {
        return entityManager.getEntitiesWithType(entityType);
    }

    public List<Entity> getEntitiesWithBoundingBox(AABBd boundingBox) {
        return entityManager.getEntitiesWithBoundingBox(boundingBox);
    }

    public List<Entity> getEntitiesWithBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return entityManager.getEntitiesWithBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public List<Entity> getEntitiesWithSphere(double centerX, double centerY, double centerZ, double radius) {
        return entityManager.getEntitiesWithSphere(centerX, centerY, centerZ, radius);
    }

    public EntityHitResult raycastEntity(Vector3fc from, Vector3fc dir, float distance) {
        return entityManager.raycastEntity(from, dir, distance);
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
    public Entity spawnEntity(String providerName, Vector3dc position) {
        return entityManager.spawnEntity(providerName, position);
    }

    public void doDestroyEntity(Entity entity) {
        entityManager.doDestroyEntity(entity);
    }

    @Override
    public long getGameTick() {
        return gameTick;
    }

    @Nonnull
    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance) {
        return collisionManager.raycastBlock(from, dir, distance);
    }

    @Nonnull
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
    public void tick() {
        physicsSystem.tick(this);
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

    @Nonnull
    @Override
    public BlockState getBlock(int x, int y, int z) {
        Chunk chunk = getChunk(x >> Chunk.CHUNK_X_BITS, y >> Chunk.CHUNK_Y_BITS, z >> Chunk.CHUNK_Z_BITS, true);
        return chunk == null ? Registries.getBlockRegistry().air().getDefaultState() : chunk.getBlock(x, y, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return getBlock(x, y, z).getPrototype() == Registries.getBlockRegistry().air();
    }

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
            this.getChunk(pos.x() >> Chunk.CHUNK_X_BITS, pos.y() >> Chunk.CHUNK_Y_BITS, pos.z() >> Chunk.CHUNK_Z_BITS, true)
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
        return setBlock(pos, Registries.getBlockRegistry().air().getDefaultState(), cause);
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
    public synchronized void unload() {
        if (unloaded) {
            return;
        }
        unloaded = true;
        chunkManager.unloadAll();
        game.doUnloadWorld(this);
    }

    @Override
    public boolean isUnloaded() {
        return unloaded;
    }

    @Override
    public void save() {
        chunkManager.saveAll();
    }

//    @Override
//    public void run() {
//        ticker.run();
//    }
//
//    public boolean isStopped() {
//        return ticker.isStopped();
//    }
//
//    public void stop() {
//        ticker.stop();
//    }

    @Override
    public WorldCommonChunkManager getChunkManager() {
        return chunkManager;
    }

    public void setChunkManager(WorldCommonChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    static final class PhysicsSystem {
        public void tick(World world) {
            for (Entity entity : world.getEntities()) {
                Vector3f motion = entity.getMotion();
                if (motion.x == 0 && motion.y == 0 && motion.z == 0)
                    continue;
                Vector3f direction = new Vector3f(motion);
                Vector3d position = entity.getPosition();
                AABBd box = entity.getBoundingBox();
                if (box == null)
                    continue;

                BlockPos localPos = BlockPos.of(((int) Math.floor(position.x)), ((int) Math.floor(position.y)),
                        ((int) Math.floor(position.z)));

//                 int directionX = motion.x == -0 ? 0 : Float.compare(motion.x, 0),
//                 directionY = motion.y == -0 ? 0 : Float.compare(motion.y, 0),
//                 directionZ = motion.z == -0 ? 0 : Float.compare(motion.z, 0);

                AABBd entityBox = box.translate(position.add(direction, new Vector3d()), new AABBd());
                List<BlockPos>[] around = AABBs.around(entityBox, motion);
                for (List<BlockPos> ls : around) {
                    ls.add(localPos);
                }
                List<BlockPos> faceX = around[0], faceY = around[1], faceZ = around[2];

                double xFix = Integer.MAX_VALUE, yFix = Integer.MAX_VALUE, zFix = Integer.MAX_VALUE;
                if (faceX.size() != 0) {
                    for (BlockPos pos : faceX) {
                        BlockState block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getPrototype().getShape().getBoundingBoxes(world, pos, block.getPrototype());
                        if (blockBoxes.length != 0)
                            for (AABBd blockBoxLocal : blockBoxes) {
                                AABBd blockBox = blockBoxLocal.translate(
                                        new Vector3f(pos.x(), pos.y(), pos.z()), new AABBd());
                                if (blockBox.intersectsAABB(entityBox)) {
                                    xFix = Math.min(xFix, Math.min(Math.abs(blockBox.maxX - entityBox.minX),
                                            Math.abs(blockBox.minX - entityBox.maxX)));
                                }
                            }
                    }
                }
                if (faceY.size() != 0) {
                    for (BlockPos pos : faceY) {
                        BlockState block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getPrototype().getShape()
                                .getBoundingBoxes(world, pos, block.getPrototype());
                        if (blockBoxes.length != 0)
                            for (AABBd blockBox : blockBoxes) {
                                AABBd translated = blockBox.translate(
                                        new Vector3f(pos.x(), pos.y(), pos.z()), new AABBd());
                                if (translated.intersectsAABB(entityBox)) {
                                    yFix = Math.min(yFix, Math.min(Math.abs(translated.maxY - entityBox.minY),
                                            Math.abs(translated.minY - entityBox.maxY)));
                                }
                            }
                    }
                }
                if (faceZ.size() != 0) {
                    for (BlockPos pos : faceZ) {
                        BlockState block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getPrototype().getShape()
                                .getBoundingBoxes(world, pos, block.getPrototype());
                        if (blockBoxes.length != 0)
                            for (AABBd blockBox : blockBoxes) {
                                AABBd translated = blockBox.translate(
                                        new Vector3f(pos.x(), pos.y(), pos.z()), new AABBd());
                                if (translated.intersectsAABB(entityBox)) {
                                    zFix = Math.min(zFix, Math.min(Math.abs(translated.maxZ - entityBox.minZ),
                                            Math.abs(translated.minZ - entityBox.maxZ)));
                                }
                            }
                    }
                }
                if (Integer.MAX_VALUE != xFix)
                    motion.x = 0;
                if (Integer.MAX_VALUE != yFix)
                    motion.y = 0;
                if (Integer.MAX_VALUE != zFix) {
                    motion.z = 0;
                }

                // if (motion.y > 0) motion.y -= 0.01f;
                // else if (motion.y < 0) motion.y += 0.01f;
                // if (Math.abs(motion.y) <= 0.01f) motion.y = 0; // physics update
            }
        }
    }

    @Override
    public boolean isLogicSide() {
        return true;
    }
}
