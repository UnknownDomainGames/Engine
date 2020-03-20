package engine.world.collision;

import engine.block.Block;
import engine.registry.Registries;
import engine.util.Direction;
import engine.world.CollisionManager;
import engine.world.World;
import engine.world.hit.BlockHitResult;
import engine.world.util.RayTraceUtils;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

public class DefaultCollisionManager implements CollisionManager {

    public static final float CALC_ERROR_FIXING = 1e-5f;

    private final World world;

    public DefaultCollisionManager(@Nonnull World world) {
        this.world = Objects.requireNonNull(world);
    }

    @Nonnull
    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance) {
        return raycastBlock(from, dir, distance, Set.of(Registries.getBlockRegistry().air()));
    }

    @Nonnull
    @Override
    public BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore) {
        var rayOffset = dir.normalize(new Vector3f()).mul(distance);
        var dist = rayOffset.add(from, new Vector3f());
        var iterator = RayTraceUtils.rayTraceBlockPos(from, dist);
        while (iterator.hasNext()) {
            var pos = iterator.next();
            var block = world.getBlock(pos);
            if (ignore.contains(block))
                continue;
            var local = from.sub(pos.x(), pos.y(), pos.z(), new Vector3f());
            var result = new Vector2d();
            if (block.getShape().intersectRay(world, pos, block,
                    local.x, local.y, local.z,
                    rayOffset.x, rayOffset.y, rayOffset.z,
                    result)) {
                var hitPoint = local.add(rayOffset.mul((float) result.x, new Vector3f()));
                Direction direction = null;
                if (hitPoint.x <= 0f + CALC_ERROR_FIXING) {
                    direction = Direction.WEST;
                } else if (hitPoint.x >= 1f - CALC_ERROR_FIXING) {
                    direction = Direction.EAST;
                } else if (hitPoint.y <= 0f + CALC_ERROR_FIXING) {
                    direction = Direction.DOWN;
                } else if (hitPoint.y >= 1f - CALC_ERROR_FIXING) {
                    direction = Direction.UP;
                } else if (hitPoint.z <= 0f + CALC_ERROR_FIXING) {
                    direction = Direction.NORTH;
                } else if (hitPoint.z >= 1f - CALC_ERROR_FIXING) {
                    direction = Direction.SOUTH;
                }
                if (direction != null) {
                    return new BlockHitResult(world, pos, block, hitPoint, direction);
                }
            }
        }
        return BlockHitResult.failure();
    }
}
