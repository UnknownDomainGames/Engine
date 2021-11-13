package engine.graphics.bounds;

import org.joml.FrustumIntersection;
import org.joml.Vector3fc;
import org.joml.primitives.Spheref;

public class BoundingSphere extends Spheref implements BoundingVolume {
    public BoundingSphere() {
    }

    public BoundingSphere(Spheref source) {
        super(source);
    }

    public BoundingSphere(Vector3fc c, float r) {
        super(c, r);
    }

    public BoundingSphere(float x, float y, float z, float r) {
        super(x, y, z, r);
    }

    @Override
    public boolean test(FrustumIntersection frustum) {
        return frustum.testSphere(x, y, z, r);
    }
}
