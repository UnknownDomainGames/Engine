package unknowndomain.engine.client.world;

import org.joml.AABBd;
import org.joml.Rayd;
import org.joml.Vector2d;
import org.joml.Vector3f;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.Chunk;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EasyWorld implements World {
    private Map<BlockPos, BlockObject> blockData = new HashMap<>();

    public EasyWorld() {
    }

    public Collection<Map.Entry<BlockPos, BlockObject>> getAllBlock() {
        return blockData.entrySet();
    }

    public BlockPos pickBeside(Vector3f from, Vector3f dir, int distance) {
        Vector3f step = dir;
        Vector3f cur = new Vector3f(from);

        for (int i = 0; i < distance; i++) {
            cur.add(step);
            BlockPos pos = new BlockPos((int) cur.x, (int) cur.y, (int) cur.z);
            BlockObject object = blockData.get(pos);
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

    public BlockPos pick(Vector3f from, Vector3f dir, int distance) {
        Vector3f step = dir;
        Vector3f cur = new Vector3f(from);

        for (int i = 0; i < distance; i++) {
            cur.add(step);
            BlockPos pos = new BlockPos((int) cur.x, (int) cur.y, (int) cur.z);
            BlockObject object = blockData.get(pos);
            if (object != null) {
                Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
                AABBd box = object.getBoundingBox();
                Vector2d result = new Vector2d();
                boolean hit = box.intersectRay(new Rayd(local.x, local.y, local.z, dir.x, dir.y, dir.z), result);
                if (hit) {
//                    Vector3f hitPoint = local.add(dir.mul((float) result.x, new Vector3f()));
                    return pos;
                }
            }
            cur.add(step);
        }
        return null;
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return null;
    }

    public BlockObject getBlock(BlockPos pos) {
        return blockData.get(pos);
    }

    @Override
    public BlockObject setBlock(BlockPos pos, BlockObject block) {
        return blockData.put(pos, block);
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
}
