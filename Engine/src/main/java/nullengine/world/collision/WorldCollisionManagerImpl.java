package nullengine.world.collision;

import com.google.common.collect.Sets;
import nullengine.block.Block;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.util.Facing;
import nullengine.world.World;
import nullengine.world.util.FastVoxelRayTrace;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

public class WorldCollisionManagerImpl implements WorldCollisionManager {

    public static final float CALC_ERROR_FIXING = 1e-5f;

    private final World world;

    public WorldCollisionManagerImpl(@Nonnull World world) {
        this.world = Objects.requireNonNull(world);
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Nonnull
    @Override
    public RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance) {
        return raycastBlock(from, dir, distance, Sets.newHashSet(Registries.getBlockRegistry().air()));
    }

    @Nonnull
    @Override
    public RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore) {
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector3f dist = rayOffset.add(from, new Vector3f());

        var all = FastVoxelRayTrace.rayTrace(from, dist);

        all.sort(Comparator.comparingDouble(pos -> from.distanceSquared(pos.getX(), pos.getY(), pos.getZ())));

        for (BlockPos pos : all) {
            Block block = world.getBlock(pos);
            if (ignore.contains(block))
                continue;
            Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
            Vector2d result = new Vector2d();
            if (block.getShape().intersectRay(world, pos, block,
                    local.x, local.y, local.z,
                    rayOffset.x, rayOffset.y, rayOffset.z,
                    result)) {
                Vector3f hitPoint = local.add(rayOffset.mul((float) result.x, new Vector3f()));
                Facing facing = null;
                if (hitPoint.x <= 0f + CALC_ERROR_FIXING) {
                    facing = Facing.WEST;
                } else if (hitPoint.x >= 1f - CALC_ERROR_FIXING) {
                    facing = Facing.EAST;
                } else if (hitPoint.y <= 0f + CALC_ERROR_FIXING) {
                    facing = Facing.DOWN;
                } else if (hitPoint.y >= 1f - CALC_ERROR_FIXING) {
                    facing = Facing.UP;
                } else if (hitPoint.z <= 0f + CALC_ERROR_FIXING) {
                    facing = Facing.NORTH;
                } else if (hitPoint.z >= 1f - CALC_ERROR_FIXING) {
                    facing = Facing.SOUTH;
                }
                if (facing != null) {
                    return new RayTraceBlockHit(world, pos, block, hitPoint, facing);
                }
            }
        }
        return RayTraceBlockHit.failure();
    }
}
