package unknowndomain.engine.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.joml.*;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.PlayerEntity;
import unknowndomain.engine.entity.TwoHands;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.FixStepTicker;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.player.PlayerImpl;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.util.FastVoxelRayCast;
import unknowndomain.engine.world.chunk.Chunk;
import unknowndomain.engine.world.chunk.ChunkStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldCommon implements World, Runnable {
    private final Game game;

    private final PhysicsSystem physicsSystem = new PhysicsSystem(); // prepare for split

    private final ChunkStorage chunkStorage;
    private final List<Player> players = new ArrayList<>();
    private final List<Entity> entityList = new ArrayList<>();
    private final List<Runnable> nextTick = new ArrayList<>();

    private final FixStepTicker ticker;
//    private ExecutorService service;

    public WorldCommon(Game game) {
        this.game = game;
        this.chunkStorage = new ChunkStorage(this);
        this.ticker = new FixStepTicker(this::tick, 20); // TODO: make tps configurable
    }

    public void spawnEntity(Entity entity) {
        BlockPos pos = BlockPos.of(entity.getPosition());
        Chunk chunk = chunkStorage.getChunk(pos);
        chunk.getEntities().add(entity);
        entityList.add(entity);
    }

    public Player playerJoin(Profile data) {
        PlayerEntity entity = new PlayerEntity(entityList.size(), ImmutableMap.<String, Object>builder()
                .put(TwoHands.class.getName(), new PlayerEntity.TwoHandImpl()).build());
        entity.getPosition().set(0, 2, 0);
        entity.getRotation().set(0, 0, 0);
        entity.setBoundingBox(data.getBoundingBox());
        spawnEntity(entity);
        PlayerImpl player = new PlayerImpl(data, this, entity);
        players.add(player);
        return player;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public List<Entity> getEntities() {
        return entityList;
    }

    @Override
    public BlockPrototype.Hit raycast(Vector3f from, Vector3f dir, float distance) {
        return raycast(from, dir, distance, Sets.newHashSet(game.getContext().getBlockRegistry().getValue(0)));
    }

    @Override
    public BlockPrototype.Hit raycast(Vector3f from, Vector3f dir, float distance, Set<Block> ignore) {
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector3f dist = rayOffset.add(from, new Vector3f());

        List<BlockPos> all;
        all = FastVoxelRayCast.ray(from, dist);

        for (BlockPos pos : all) {
            Block object = getBlock(pos);
            if (ignore.contains(object))
                continue;
            Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
            AABBd[] boxes = object.getBoundingBoxes();
            Vector2d result = new Vector2d();
            for (AABBd box : boxes) {
                boolean hit = box.intersectRay(local.x, local.y, local.z, rayOffset.x, rayOffset.y, rayOffset.z,
                        result);
                if (hit) {
                    Vector3f hitPoint = local.add(rayOffset.mul((float) result.x, new Vector3f()));
                    Facing facing = Facing.NORTH;
                    if (hitPoint.x == 0f) {
                        facing = Facing.WEST;
                    } else if (hitPoint.x == 1f) {
                        facing = Facing.EAST;
                    } else if (hitPoint.y == 0f) {
                        facing = Facing.BOTTOM;
                    } else if (hitPoint.y == 1f) {
                        facing = Facing.TOP;
                    } else if (hitPoint.z == 0f) {
                        facing = Facing.SOUTH;
                    } else if (hitPoint.z == 1f) {
                        facing = Facing.NORTH;
                    }
                    return new BlockPrototype.Hit(pos, object, hitPoint, facing);
                }
            }
        }
        return null;
    }

    protected void tick() {
        if (nextTick.size() != 0) {
            for (Runnable tick : nextTick) { // TODO: limit time
                tick.run();
            }
        }
        physicsSystem.tick(this);

        for (Entity entity : this.getEntities()) {
            Vector3d position = entity.getPosition();
            Vector3f motion = entity.getMotion();
            BlockPos oldPosition = BlockPos.of(position);
            position.add(motion);
            BlockPos newPosition = BlockPos.of(position);

            if (!BlockPos.inSameChunk(oldPosition, newPosition)) {
                Chunk oldChunk = chunkStorage.getChunk(oldPosition), newChunk = chunkStorage.getChunk(newPosition);
                oldChunk.getEntities().remove(entity);
                newChunk.getEntities().add(entity);
                // entity leaving and enter chunk event
            }
        }

        chunkStorage.getChunks().forEach(this::tickChunk);
        for (Entity entity : entityList)
            entity.tick(); // state machine update
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
        return chunkStorage.getChunk(x, y, z).getBlock(x, y, z);
    }

    @Nonnull
    @Override
    public Block setBlock(int x, int y, int z, @Nonnull Block block) {
        return chunkStorage.getChunk(x, y, z).setBlock(x, y, z, block);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkStorage.getChunk(chunkX, chunkY, chunkZ);
    }

    @Override
    public void run() {
        ticker.start();
    }

    public boolean isStop() {
        return ticker.isStop();
    }

    public void stop() {
        ticker.stop();
    }

    static class PhysicsSystem {
        public void tick(World world) {
            List<Entity> entityList = world.getEntities();
            for (Entity entity : entityList) {
                Vector3f motion = entity.getMotion();
                Vector3f direction = new Vector3f(motion);
                if (motion.x == 0 && motion.y == 0 && motion.z == 0)
                    continue;
                Vector3d position = entity.getPosition();
                AABBd box = entity.getBoundingBox();

                BlockPos localPos = BlockPos.of(((int) Math.floor(position.x)), ((int) Math.floor(position.y)),
                        ((int) Math.floor(position.z)));
                //
                // int directionX = motion.x == -0 ? 0 : Float.compare(motion.x, 0),
                // directionY = motion.y == -0 ? 0 : Float.compare(motion.y, 0),
                // directionZ = motion.z == -0 ? 0 : Float.compare(motion.z, 0);

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
                        AABBd[] blockBoxes = block.getBoundingBoxes();
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
                        AABBd[] blockBoxes = block.getBoundingBoxes();
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
                        AABBd[] blockBoxes = block.getBoundingBoxes();
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

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }

    public static class ChunkUnload implements Event {
        public final Vector3i pos;

        public ChunkUnload(Vector3i pos) {
            this.pos = pos;
        }
    }
}
