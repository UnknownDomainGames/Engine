package engine.graphics.bounds;

import org.joml.FrustumIntersection;

public interface BoundingVolume {
    boolean test(FrustumIntersection frustum);
}