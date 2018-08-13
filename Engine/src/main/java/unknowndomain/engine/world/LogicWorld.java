package unknowndomain.engine.world;

import com.google.common.collect.Sets;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.AABBd;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import unknowndomain.engine.Entity;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.util.FastVoxelRayCast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LogicWorld implements World {
    private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();
    private ChunkProvider chunkProvider = new ChunkProviderDummy();
    private List<Entity> entityList = new ArrayList<>();
    private GameContext context;

    public LogicWorld(GameContext context) {
        this.context = context;
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    @Override
    public BlockPrototype.Hit rayHit(Vector3f from, Vector3f dir, float distance) {
        return rayHit(from, dir, distance, Sets.newHashSet(context.getBlockRegistry().getValue(0)));
    }

    @Override
    public BlockPrototype.Hit rayHit(Vector3f from, Vector3f dir, float distance, Set<Block> ignore) {
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector3f dist = rayOffset.add(from, new Vector3f());

        List<BlockPos> all;
        all = FastVoxelRayCast.ray(from, dist);

        for (BlockPos pos : all) {
            Block object = getBlock(pos);
            if (ignore.contains(object)) continue;
            Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
            AABBd[] boxes = object.getBoundingBoxes();
            Vector2d result = new Vector2d();
            for (AABBd box : boxes) {
                boolean hit = box.intersectRay(local.x, local.y, local.z, rayOffset.x, rayOffset.y, rayOffset.z, result);
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

    public void tick() {
        for (Entity entity : entityList) {
            Vector3f motion = new Vector3f(entity.getMotion());
            Vector3f direction = new Vector3f(motion);
            if (motion.x == 0 && motion.y == 0 && motion.z == 0) continue;
            Vector3f position = entity.getPosition();
            AABBd box = entity.getBoundingBox();

           BlockPos localPos = new BlockPos(((int) Math.floor(position.x)), ((int) Math.floor(position.y)), ((int) Math.floor(position.z)));
//
            int directionX = motion.x == -0 ? 0 : Float.compare(motion.x, 0),
                    directionY = motion.y == -0 ? 0 : Float.compare(motion.y, 0),
                    directionZ = motion.z == -0 ? 0 : Float.compare(motion.z, 0);

            AABBd entityBox = AABBs.translate(box, position.add(direction, new Vector3f()), new AABBd());
            List<BlockPos>[] around = AABBs.around(entityBox, motion);
           for (List<BlockPos> ls : around) {
               ls.add(localPos);
           }
            List<BlockPos> faceX = around[0],
                    faceY = around[1],
                    faceZ = around[2];

            double xFix = Integer.MAX_VALUE,
                    yFix = Integer.MAX_VALUE,
                    zFix = Integer.MAX_VALUE;
            if (faceX.size() != 0) {
                for (BlockPos pos : faceX) {
                    Block block = getBlock(pos);
                    AABBd[] blockBoxes = block.getBoundingBoxes();
                    if (blockBoxes.length != 0)
                        for (AABBd blockBoxLocal : blockBoxes) {
                            AABBd blockBox = AABBs.translate(blockBoxLocal, new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (blockBox.testAABB(entityBox)) {
                                xFix = Math.min(xFix, Math.min(Math.abs(blockBox.maxX - entityBox.minX), Math.abs(blockBox.minX - entityBox.maxX)));
                            }
                        }
                }
            }
            if (faceY.size() != 0) {
                for (BlockPos pos : faceY) {
                    Block block = getBlock(pos);
                    AABBd[] blockBoxes = block.getBoundingBoxes();
                    if (blockBoxes.length != 0)
                        for (AABBd blockBox : blockBoxes) {
                            AABBd translated = AABBs.translate(blockBox, new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (translated.testAABB(entityBox)) {
                                yFix = Math.min(yFix, Math.min(Math.abs(translated.maxY - entityBox.minY), Math.abs(translated.minY - entityBox.maxY)));
                            }
                        }
                }
            }
            if (faceZ.size() != 0) {
                for (BlockPos pos : faceZ) {
                    Block block = getBlock(pos);
                    AABBd[] blockBoxes = block.getBoundingBoxes();
                    if (blockBoxes.length != 0)
                        for (AABBd blockBox : blockBoxes) {
                            AABBd translated = AABBs.translate(blockBox, new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (translated.testAABB(entityBox)) {
                                zFix = Math.min(zFix, Math.min(Math.abs(translated.maxZ - entityBox.minZ), Math.abs(translated.minZ - entityBox.maxZ)));
                            }
                        }
                }
            }
            if (Integer.MAX_VALUE != xFix)
                motion.x = 0;
            if (Integer.MAX_VALUE != yFix)
                motion.y = 0;
            if (Integer.MAX_VALUE != zFix) {
                System.out.println(zFix);
                motion.z += directionZ > 0 ? -zFix : zFix;
                // motion.z = 0;
            }
//                motion.z += directionZ > 0 ? -zFix : zFix;

            position.add(motion);
//            if (motion.y > 0) motion.y -= 0.01f;
//            else if (motion.y < 0) motion.y += 0.01f;
//            if (Math.abs(motion.y) <= 0.01f) motion.y = 0; // physics update
        }
        chunks.forEach(this::tickChunk);

        for (Entity entity : entityList) {
            entity.tick(); // state machine update
        }
    }

    private void tickChunk(long pos, Chunk chunk) {
        Collection<Block> blocks = chunk.getRuntimeBlock();
        if (blocks.size() != 0) {
            for (Block object : blocks) {
                BlockPrototype.TickBehavior behavior = object.getBehavior(BlockPrototype.TickBehavior.class);
                if (behavior != null) {
                    behavior.tick(object);
                }
            }
        }
    }

    @Override
    public Chunk getChunk(int x, int z) {
        long pos = (long) x << 32 | z;
        return chunks.get(pos);
    }

    public Block getBlock(BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null)
            return chunk.getBlock(pos);
        else {
            Chunk nchunk = chunkProvider.provideChunk(this.context, pos); // TODO async load
            chunks.put(cp, nchunk);
            return nchunk.getBlock(pos);
        }
    }

    @Override
    public Block setBlock(BlockPos pos, Block block) {
        ChunkPos chunkPos = pos.toChunk();
        long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null)
            chunk.setBlock(pos, block);
        else {
            Chunk nchunk = chunkProvider.provideChunk(this.context, pos); // TODO async load
            this.chunks.put(cp, nchunk);
            nchunk.setBlock(pos, block);
        }
        return null;
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

    public static class ChunkLoad implements Event {
        public final ChunkPos pos;
        public final int[][] blocks;

        public ChunkLoad(ChunkPos pos, int[][] blocks) {
            this.pos = pos;
            this.blocks = blocks;
        }
    }

    public static class ChunkUnload implements Event {
        public final Vector3i pos;

        public ChunkUnload(Vector3i pos) {
            this.pos = pos;
        }
    }
}
