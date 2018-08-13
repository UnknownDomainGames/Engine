package unknowndomain.engine.math;

import org.joml.AABBd;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class AABBs {
    public static AABBd translate(AABBd aabBd, Vector3f translate) {
        return translate(aabBd, translate, aabBd);
    }

    public static AABBd translate(AABBd aabBd, Vector3f translate, AABBd result) {
        result.minX = aabBd.minX + translate.x;
        result.minY = aabBd.minY + translate.y;
        result.minZ = aabBd.minZ + translate.z;

        result.maxX = aabBd.maxX + translate.x;
        result.maxY = aabBd.maxY + translate.y;
        result.maxZ = aabBd.maxZ + translate.z;
        return result;
    }

    public static List<BlockPos>[] around(AABBd aabb, Vector3f movement) {
        List<BlockPos>[] offsets = new List[3];
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = new ArrayList<>();
        }

        int directionX = movement.x == -0 ? 0 : Float.compare(movement.x, 0),
                directionY = movement.y == -0 ? 0 : Float.compare(movement.y, 0),
                directionZ = movement.z == -0 ? 0 : Float.compare(movement.z, 0);

        if (directionX != 0) {
            double src = directionX == -1 ? aabb.maxX : directionX == 1 ? aabb.minX : 0;
            for (double i = aabb.minZ; i <= aabb.maxZ; i++)
                for (double j = aabb.minY; j <= aabb.maxY; j++)
                    offsets[0].add(
                            new BlockPos((int) Math.floor(src + directionX), (int) Math.floor(j), (int) Math.floor(i)));
        }
        if (directionY != 0) {
            double src = directionY == -1 ? aabb.maxY : directionY == 1 ? aabb.minY : 0;
            for (double i = aabb.minZ; i <= aabb.maxZ; i++)
                for (double j = aabb.minX; j <= aabb.maxX; j++)
                    offsets[1].add(
                            new BlockPos((int) Math.floor(j), (int) Math.floor(src + directionY), (int) Math.floor(i)));
        }
        if (directionZ != 0) {
            double src = directionZ == -1 ? aabb.maxZ : directionZ == 1 ? aabb.minZ : 0;
            for (double i = aabb.minY; i <= aabb.maxY; i++)
                for (double j = aabb.minX; j <= aabb.maxX; j++)
                    offsets[2].add(
                            new BlockPos((int) Math.floor(j), (int) Math.floor(i), (int) Math.floor(src + directionZ)));
        }
        return offsets;
    }
}
