package unknowndomain.engine.math;

import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class AABBs {
    public static AABBd translate(AABBd aabBd, Vector3d translate) {
        return translate(aabBd, translate, aabBd);
    }

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

    public static AABBd translate(AABBd aabBd, Vector3d translate, AABBd result) {
        result.minX = aabBd.minX + translate.x;
        result.minY = aabBd.minY + translate.y;
        result.minZ = aabBd.minZ + translate.z;

        result.maxX = aabBd.maxX + translate.x;
        result.maxY = aabBd.maxY + translate.y;
        result.maxZ = aabBd.maxZ + translate.z;
        return result;
    }

    @SuppressWarnings("unchecked")
    public static List<BlockPos>[] around(AABBd aabb, Vector3f movement) {
        List<BlockPos>[] offsets = new List[3];
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = new ArrayList<>();
        }
//        aabb = translate(aabb, movement, new AABBd());

        int directionX = movement.x == -0 ? 0 : Float.compare(movement.x, 0),
                directionY = movement.y == -0 ? 0 : Float.compare(movement.y, 0),
                directionZ = movement.z == -0 ? 0 : Float.compare(movement.z, 0);
        int minX = (int) Math.floor(aabb.minX),
                minY = (int) Math.floor(aabb.minY),
                minZ = (int) Math.floor(aabb.minZ),
                maxX = (int) Math.floor(aabb.maxX),
                maxY = (int) Math.floor(aabb.maxY),
                maxZ = (int) Math.floor(aabb.maxZ);

        if (directionX != 0) {
            double src = directionX == -1 ? minX : directionX == 1 ? maxX : 0 + movement.x;
            for (double i = minZ; i <= maxZ; i++)
                for (double j = minY; j <= maxY; j++)
                    offsets[0].add(BlockPos.of((int) Math.floor(src), (int) Math.floor(j), (int) Math.floor(i)));
        }
        if (directionY != 0) {
            double src = directionY == -1 ? minY : directionY == 1 ? maxY : 0 + movement.y;
            for (double i = minZ; i <= maxZ; i++)
                for (double j = minX; j <= maxX; j++)
                    offsets[1].add(BlockPos.of((int) Math.floor(j), (int) Math.floor(src), (int) Math.floor(i)));
        }
        if (directionZ != 0) {
            double src = directionZ == -1 ? minZ : directionZ == 1 ? maxZ : 0 + movement.z;
            for (double i = minY; i <= maxY; i++)
                for (double j = minX; j <= maxX; j++)
                    offsets[2].add(BlockPos.of((int) Math.floor(j), (int) Math.floor(i), (int) Math.floor(src)));
        }
        return offsets;
    }
}
