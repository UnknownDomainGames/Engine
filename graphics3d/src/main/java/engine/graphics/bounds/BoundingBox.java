package engine.graphics.bounds;

import org.joml.FrustumIntersection;
import org.joml.Vector3fc;
import org.joml.primitives.AABBf;

public class BoundingBox extends AABBf implements BoundingVolume {
    public BoundingBox() {
    }

    public BoundingBox(AABBf source) {
        super(source);
    }

    public BoundingBox(Vector3fc min, Vector3fc max) {
        super(min, max);
    }

    public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean test(FrustumIntersection frustum) {
        return frustum.testAab(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
