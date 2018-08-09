package unknowndomain.engine.world;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.*;
import unknowndomain.engine.Entity;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.Facing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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


    public Block.Hit rayHit(Vector3f from, Vector3f dir, int distance) {
        Vector3f step = dir;
        Vector3f cur = new Vector3f(from);

        for (int i = 0; i < distance; i++) {
            cur.add(step);
            BlockPos pos = new BlockPos((int) cur.x, (int) cur.y, (int) cur.z);
            BlockObject object = getBlock(pos);
            if (object != null) {
                Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
                AABBd box = object.getBoundingBox();
                Vector2d result = new Vector2d();
                boolean hit = box.intersectRay(new Rayd(local.x, local.y, local.z, dir.x, dir.y, dir.z), result);
                if (hit) {
                    Vector3f hitPoint = local.add(dir.mul((float) result.x, new Vector3f()));
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
                    return new Block.Hit(pos, object, hitPoint, facing);
                }
            }
            cur.add(step);
        }
        return null;
    }

    public BlockPos pickBeside(Vector3f from, Vector3f dir, int distance) {
        Vector3f step = dir;
        Vector3f cur = new Vector3f(from);

        for (int i = 0; i < distance; i++) {
            cur.add(step);
            BlockPos pos = new BlockPos((int) cur.x, (int) cur.y, (int) cur.z);
            BlockObject object = getBlock(pos);
            if (object != null) {
                Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
                AABBd box = object.getBoundingBox();
                Vector2d result = new Vector2d();
                boolean hit = box.intersectRay(new Rayd(local.x, local.y, local.z, dir.x, dir.y, dir.z), result);
                if (hit) {
                    Vector3f hitPoint = local.add(dir.mul((float) result.x, new Vector3f()));
                    if (hitPoint.x == 0f) {
                        return pos.add(-1, 0, 0);
                    } else if (hitPoint.x == 1f) {
                        return pos.add(1, 0, 0);
                    } else if (hitPoint.y == 0f) {
                        return pos.add(0, -1, 0);
                    } else if (hitPoint.y == 1f) {
                        return pos.add(0, 1, 0);
                    } else if (hitPoint.z == 0f) {
                        return pos.add(0, 0, -1);
                    } else if (hitPoint.z == 1f) {
                        return pos.add(0, 0, 1);
                    }
                    return pos;
                }
            }
            cur.add(step);
        }
        return null;
    }


    public void tick() {
        for (Entity entity : entityList) {
            Vector3f motion = entity.getMotion();

            if (motion.y > 0) motion.y -= 0.01f;
            else if (motion.y < 0) motion.y += 0.01f;
            if (Math.abs(motion.y) <= 0.01f) motion.y = 0; // physics update
        }
        chunks.forEach(this::tickChunk);

        for (Entity entity : entityList) {
            entity.tick(); // state machine update
        }
    }

    private void tickChunk(long pos, Chunk chunk) {
        Collection<BlockObject> blockObjects = chunk.getRuntimeBlock();
        if (blockObjects.size() != 0) {
            for (BlockObject object : blockObjects) {
                Block.TickBehavior behavior = object.getBehavior(Block.TickBehavior.class);
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

    public BlockObject getBlock(BlockPos pos) {
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
    public BlockObject setBlock(BlockPos pos, BlockObject block) {
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
