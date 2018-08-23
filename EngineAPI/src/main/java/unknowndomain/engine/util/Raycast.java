package unknowndomain.engine.util;

import org.joml.AABBd;
import org.joml.Vector2d;
import org.joml.Vector3f;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import java.util.List;
import java.util.function.Predicate;

public class Raycast {
    public static BlockPrototype.Hit raycast(World world, Vector3f from, Vector3f dir, float distance, Predicate<Block> ignore) {
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector3f dist = rayOffset.add(from, new Vector3f());

        List<BlockPos> all;
        all = FastVoxelRayCast.ray(from, dist);

        for (BlockPos pos : all) {
            Block object = world.getBlock(pos);
            if (ignore.test(object))
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
}
