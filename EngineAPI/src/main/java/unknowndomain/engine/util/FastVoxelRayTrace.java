package unknowndomain.engine.util;

import org.joml.Vector3fc;
import org.joml.Vector3i;
import unknowndomain.engine.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * https://stackoverflow.com/questions/12367071/how-do-i-initialize-the-t-variables-in-a-fast-voxel-traversal-algorithm-for-ray
 */
public class FastVoxelRayTrace {
    private static float frac0(float x) {
        return x - (float) Math.floor(x);
    }

    private static float frac1(float x) {
        return 1 - x + (float) Math.floor(x);
    }

    public static List<BlockPos> rayTrace(Vector3fc start, Vector3fc end) {
        List<BlockPos> container = new ArrayList<>();
        float tMaxX, tMaxY, tMaxZ, tDeltaX, tDeltaY, tDeltaZ;
        Vector3i voxel = new Vector3i();

        float x1 = start.x(), y1 = start.y(), z1 = start.z(); // start point
        float x2 = end.x(), y2 = end.y(), z2 = end.z(); // end point

        int dx = Float.compare((x2 - x1), 0);
        if (dx != 0) tDeltaX = Math.min(dx / (x2 - x1), 10000000.0f);
        else tDeltaX = 10000000.0f;
        if (dx > 0) tMaxX = tDeltaX * frac1(x1);
        else tMaxX = tDeltaX * frac0(x1);
        voxel.x = (int) Math.floor(x1);

        int dy = Float.compare(y2 - y1, 0);
        if (dy != 0) tDeltaY = Math.min(dy / (y2 - y1), 10000000.0f);
        else tDeltaY = 10000000.0f;
        if (dy > 0) tMaxY = tDeltaY * frac1(y1);
        else tMaxY = tDeltaY * frac0(y1);
        voxel.y = (int) Math.floor(y1);

        int dz = Float.compare(z2 - z1, 0);
        if (dz != 0) tDeltaZ = Math.min(dz / (z2 - z1), 10000000.0f);
        else tDeltaZ = 10000000.0f;
        if (dz > 0) tMaxZ = tDeltaZ * frac1(z1);
        else tMaxZ = tDeltaZ * frac0(z1);
        voxel.z = (int) Math.floor(z1);

        while (true) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    voxel.x += dx;
                    tMaxX += tDeltaX;
                } else {
                    voxel.z += dz;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    voxel.y += dy;
                    tMaxY += tDeltaY;
                } else {
                    voxel.z += dz;
                    tMaxZ += tDeltaZ;
                }
            }
            if (tMaxX > 1 && tMaxY > 1 && tMaxZ > 1) break;
            container.add(BlockPos.of(voxel.x, voxel.y, voxel.z));
        }
        return container;
    }
}
