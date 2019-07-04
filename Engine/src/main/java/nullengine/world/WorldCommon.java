package nullengine.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.entity.PlayerEntity;
import nullengine.event.Event;
import nullengine.event.EventBus;
import nullengine.event.world.WorldTickEvent;
import nullengine.event.world.block.*;
import nullengine.game.Game;
import nullengine.math.AABBs;
import nullengine.math.BlockPos;
import nullengine.math.FixStepTicker;
import nullengine.player.Player;
import nullengine.registry.Registries;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.ChunkManager;
import nullengine.world.chunk.WorldCommonChunkManager;
import nullengine.world.collision.WorldCollisionManager;
import nullengine.world.collision.WorldCollisionManagerImpl;
import nullengine.world.gen.ChunkGenerator;
import nullengine.world.storage.WorldCommonLoader;
import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import unknowndomaingame.foundation.init.Blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldCommon implements World, Runnable {
    private final Game game;

    private final PhysicsSystem physicsSystem = new PhysicsSystem(); // prepare for split
    private final WorldCollisionManager collisionManager = new WorldCollisionManagerImpl(this);

    //private final ChunkStorage chunkStorage;
    private WorldCommonLoader loader;
    private WorldCommonChunkManager chunkManager;
    private final List<Long> criticalChunks;
    private final List<Player> players = new ArrayList<>();
    private final List<Entity> entityList = new ArrayList<>();
    private final List<Runnable> nextTick = new ArrayList<>();
    private WorldInfo worldInfo;

    private final FixStepTicker ticker;
    private long gameTick;
//    private ExecutorService service;

    public WorldCommon(Game game, WorldCommonLoader loader, ChunkGenerator chunkGenerator) {
        this.game = game;
        //this.chunkStorage = new ChunkStorage(this);
        this.loader = loader;
        this.chunkManager = new WorldCommonChunkManager(this, chunkGenerator);
        this.ticker = new FixStepTicker(this::tick, FixStepTicker.LOGIC_TICK); // TODO: make tps configurable
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
    public WorldInfo getWorldInfo() {
        return worldInfo;
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

        game.getEventBus().post(new WorldTickEvent(this));
        tickEntities();
        gameTick++;
    }

    @Override
    public long getGameTick() {
        return gameTick;
    }

    protected void tickEntities() {
        for (Entity entity : entityList)
            entity.tick(); // state machine update
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
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block) {
        Block oldBlock = getBlock(pos);
        EventBus eventBus = getGame().getEventBus();

        Event preEvent;
        if (block == Blocks.AIR) {
            preEvent = new BlockDestroyEvent.Pre(this, pos, oldBlock);
        } else if (oldBlock == Blocks.AIR) {
            preEvent = new BlockPlaceEvent.Pre(this, pos, block);
        } else {
            preEvent = new BlockReplaceEvent.Pre(this, pos, oldBlock, block);
        }
        if (!eventBus.post(preEvent)) {
            chunkManager.loadChunk(pos.getX() >> ChunkConstants.BITS_X, pos.getY() >> ChunkConstants.BITS_Y, pos.getZ() >> ChunkConstants.BITS_Z)
                    .setBlock(pos, block);

            Event postEvent;
            if (block == Blocks.AIR) {
                postEvent = new BlockDestroyEvent.Post(this, pos, oldBlock);
            } else if (oldBlock == Blocks.AIR) {
                postEvent = new BlockPlaceEvent.Post(this, pos, block);
            } else {
                postEvent = new BlockReplaceEvent.Post(this, pos, oldBlock, block);
            }
            eventBus.post(postEvent);
        }
        return oldBlock;
    }

    @Override
    public boolean interactBlock(@Nonnull BlockPos pos, @Nonnull Block block, Vector3fc localPos) {
        EventBus eventBus = getGame().getEventBus();

        if (eventBus.post(new BlockInteractEvent.Pre(this, pos, block, localPos))) {
            return eventBus.post(new BlockInteractEvent.Post(this, pos, block, localPos));
        }
        return false;
    }

    @Override
    public Block removeBlock(@Nonnull BlockPos pos) {
        return setBlock(pos, Blocks.AIR);
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
        ticker.start();
    }

    public boolean isStopped() {
        return ticker.isStop();
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

    public ChunkManager<World> getChunkManager() {
        return ((ChunkManager<World>) (Object) chunkManager);
    }

    public void setChunkManager(WorldCommonChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }
}
