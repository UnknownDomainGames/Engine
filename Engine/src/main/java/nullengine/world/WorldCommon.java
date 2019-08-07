package nullengine.world;

import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.block.component.DestroyBehavior;
import nullengine.block.component.NeighborChangeListener;
import nullengine.block.component.PlaceBehavior;
import nullengine.entity.Entity;
import nullengine.entity.PlayerEntity;
import nullengine.event.world.block.BlockChangeEvent;
import nullengine.event.world.block.BlockDestroyEvent;
import nullengine.event.world.block.BlockPlaceEvent;
import nullengine.event.world.block.BlockReplaceEvent;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.game.Game;
import nullengine.logic.Ticker;
import nullengine.math.AABBs;
import nullengine.math.BlockPos;
import nullengine.player.Player;
import nullengine.registry.Registries;
import nullengine.util.Facing;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.WorldCommonChunkManager;
import nullengine.world.collision.WorldCollisionManager;
import nullengine.world.collision.WorldCollisionManagerImpl;
import nullengine.world.gen.ChunkGenerator;
import nullengine.world.storage.WorldCommonLoader;
import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldCommon implements World, Runnable {

    private final Game game;
    private final WorldProvider provider;
    private final Path storagePath;
    private final WorldCreationSetting creationSetting;

    private final PhysicsSystem physicsSystem = new PhysicsSystem(); // prepare for split
    private final WorldCollisionManager collisionManager = new WorldCollisionManagerImpl(this);

    //private final ChunkStorage chunkStorage;
    private WorldCommonLoader loader;
    private WorldCommonChunkManager chunkManager;
    private final List<Long> criticalChunks;
    private final List<Player> players = new ArrayList<>();
    private final List<Entity> entityList = new ArrayList<>();
    private final List<Runnable> nextTick = new ArrayList<>();

    private final Ticker ticker;
    private long gameTick;
//    private ExecutorService service;

    public WorldCommon(Game game, WorldProvider provider, Path storagePath, WorldCreationSetting creationSetting, WorldCommonLoader loader, ChunkGenerator chunkGenerator) {
        this.game = game;
        this.provider = provider;
        this.storagePath = storagePath;
        this.creationSetting = creationSetting;
        //this.chunkStorage = new ChunkStorage(this);
        this.loader = loader;
        this.chunkManager = new WorldCommonChunkManager(this, chunkGenerator);
        this.ticker = new Ticker(this::tick, Ticker.LOGIC_TICK); // TODO: make tps configurable
        criticalChunks = new ArrayList<>();
    }

    public void spawnEntity(Entity entity) {
        BlockPos pos = ChunkConstants.toChunkPos(BlockPos.of(entity.getPosition()));
        Chunk chunk = chunkManager.loadChunk(pos.getX(), pos.getY(), pos.getZ());
        chunk.getEntities().add(entity);
        entityList.add(entity);
    }

    @Deprecated
    public void playerJoin(Player player) {
        // FIXME:
        var entity = new PlayerEntity(entityList.size(), this);
        player.controlEntity(entity);
        spawnEntity(entity);
        players.add(player);
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
        return null;
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
    public List<Entity> getEntities() {
        return entityList;
    }

    /**
     * Get the list of chunkpos which the corresponding chunk should be force loaded
     *
     * @return
     */
    public List<Long> getCriticalChunks() {
        return criticalChunks;
    }

    @Override
    public WorldCollisionManager getCollisionManager() {
        return collisionManager;
    }

    protected void tick() {
        if (nextTick.size() != 0) {
            for (Runnable tick : nextTick) { // TODO: limit time
                tick.run();
            }
        }
        physicsSystem.tick(this);
        tickEntityMotion();
        tickChunks();
        tickEntities();
        gameTick++;
    }

    @Override
    public long getGameTick() {
        return gameTick;
    }

    protected void tickChunks() {
        chunkManager.getChunks().forEach(this::tickChunk);
    }

    protected void tickEntities() {
        for (Entity entity : entityList)
            entity.tick(); // state machine update
    }

    protected void tickEntityMotion() {
        for (Entity entity : this.getEntities()) {
            Vector3d position = entity.getPosition();
            Vector3f motion = entity.getMotion();
            BlockPos oldPosition = ChunkConstants.toChunkPos(BlockPos.of(position));
            position.add(motion);
            BlockPos newPosition = ChunkConstants.toChunkPos(BlockPos.of(position));

            if (!BlockPos.inSameChunk(oldPosition, newPosition)) {
                Chunk oldChunk = chunkManager.loadChunk(oldPosition.getX(), oldPosition.getY(), oldPosition.getZ()),
                        newChunk = chunkManager.loadChunk(newPosition.getX(), newPosition.getY(), newPosition.getZ());
                oldChunk.getEntities().remove(entity);
                newChunk.getEntities().add(entity);
                // entity leaving and enter chunk event
            }
        }
    }

    private void tickChunk(Chunk chunk) {
//        Collection<Block> blocks = chunk.getRuntimeBlock();
//        if (blocks.size() != 0) {
//            for (Block object : blocks) {
//                BlockPrototype.TickBehavior behavior = object.getBehavior(BlockPrototype.TickBehavior.class);
//                if (behavior != null) {
//                    behavior.tick(object);
//                }
//            }
//        }
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z) {
        Chunk chunk = chunkManager.loadChunk(x >> ChunkConstants.BITS_X, y >> ChunkConstants.BITS_Y, z >> ChunkConstants.BITS_Z);
        return chunk == null ? Registries.getBlockRegistry().air() : chunk.getBlock(x, y, z);
    }

    @Nonnull
    @Override
    public int getBlockId(int x, int y, int z) {
        Chunk chunk = chunkManager.loadChunk(x >> ChunkConstants.BITS_X, y >> ChunkConstants.BITS_Y, z >> ChunkConstants.BITS_Z);
        return chunk == null ? Registries.getBlockRegistry().air().getId() : chunk.getBlockId(x, y, z);
    }

    @Nonnull
    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        Block oldBlock = getBlock(pos);
        BlockChangeEvent pre, post;
        if (block == AirBlock.AIR) {
            pre = new BlockDestroyEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockDestroyEvent.Post(this, pos, oldBlock, block, cause);
        } else if (oldBlock == AirBlock.AIR) {
            pre = new BlockPlaceEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockPlaceEvent.Post(this, pos, oldBlock, block, cause);
        } else {
            pre = new BlockReplaceEvent.Pre(this, pos, oldBlock, block, cause);
            post = new BlockReplaceEvent.Post(this, pos, oldBlock, block, cause);
        }
        if (!getGame().getEventBus().post(pre)) {
            chunkManager.loadChunk(pos.getX() >> ChunkConstants.BITS_X, pos.getY() >> ChunkConstants.BITS_Y, pos.getZ() >> ChunkConstants.BITS_Z)
                    .setBlock(pos, block, cause);

            oldBlock.getComponent(DestroyBehavior.class).ifPresent(destroyBehavior -> destroyBehavior.onDestroyed(this, pos, oldBlock, cause));
            block.getComponent(PlaceBehavior.class).ifPresent(placeBehavior -> placeBehavior.onPlaced(this, pos, block, cause));

            getGame().getEventBus().post(post);
            notifyNeighborChanged(pos, block, cause);
        }
        return oldBlock;
    }

    protected void notifyNeighborChanged(BlockPos pos, Block block, BlockChangeCause cause) {
        for (Facing facing : Facing.values()) {
            BlockPos neighborPos = pos.offset(facing);
            Block neighbor = getBlock(neighborPos);
            neighbor.getComponent(NeighborChangeListener.class)
                    .ifPresent(listener -> listener.onNeighborChanged(this, neighborPos, neighbor, facing.opposite(), pos, block, cause));
        }
    }

    @Nonnull
    @Override
    public Block destoryBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause) {
        return setBlock(pos, Registries.getBlockRegistry().air(), cause);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkManager.loadChunk(chunkX, chunkY, chunkZ);
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return chunkManager.getChunks();
    }

    @Override
    public void run() {
        ticker.run();
    }

    public boolean isStopped() {
        return ticker.isStopped();
    }

    public void stop() {
        ticker.stop();
    }

    public WorldCommonLoader getLoader() {
        return loader;
    }

    public void setLoader(WorldCommonLoader loader) {
        this.loader = loader;
    }

    public WorldCommonChunkManager getChunkManager() {
        return chunkManager;
    }

    public void setChunkManager(WorldCommonChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    static class PhysicsSystem {
        public void tick(World world) {
            List<Entity> entityList = world.getEntities();
            for (Entity entity : entityList) {
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

                AABBd entityBox = AABBs.translate(box, position.add(direction, new Vector3d()), new AABBd());
                List<BlockPos>[] around = AABBs.around(entityBox, motion);
                for (List<BlockPos> ls : around) {
                    ls.add(localPos);
                }
                List<BlockPos> faceX = around[0], faceY = around[1], faceZ = around[2];

                double xFix = Integer.MAX_VALUE, yFix = Integer.MAX_VALUE, zFix = Integer.MAX_VALUE;
                if (faceX.size() != 0) {
                    for (BlockPos pos : faceX) {
                        Block block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getShape().getBoundingBoxes(world, pos, block);
                        if (blockBoxes.length != 0)
                            for (AABBd blockBoxLocal : blockBoxes) {
                                AABBd blockBox = AABBs.translate(blockBoxLocal,
                                        new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                                if (blockBox.testAABB(entityBox)) {
                                    xFix = Math.min(xFix, Math.min(Math.abs(blockBox.maxX - entityBox.minX),
                                            Math.abs(blockBox.minX - entityBox.maxX)));
                                }
                            }
                    }
                }
                if (faceY.size() != 0) {
                    for (BlockPos pos : faceY) {
                        Block block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getShape()
                                .getBoundingBoxes(world, pos, block);
                        if (blockBoxes.length != 0)
                            for (AABBd blockBox : blockBoxes) {
                                AABBd translated = AABBs.translate(blockBox,
                                        new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                                if (translated.testAABB(entityBox)) {
                                    yFix = Math.min(yFix, Math.min(Math.abs(translated.maxY - entityBox.minY),
                                            Math.abs(translated.minY - entityBox.maxY)));
                                }
                            }
                    }
                }
                if (faceZ.size() != 0) {
                    for (BlockPos pos : faceZ) {
                        Block block = world.getBlock(pos);
                        AABBd[] blockBoxes = block.getShape()
                                .getBoundingBoxes(world, pos, block);
                        if (blockBoxes.length != 0)
                            for (AABBd blockBox : blockBoxes) {
                                AABBd translated = AABBs.translate(blockBox,
                                        new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                                if (translated.testAABB(entityBox)) {
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
}
