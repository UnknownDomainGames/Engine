package unknowndomain.engine.math;

public abstract class BoundingBox {
    public abstract Vector3d getCentre();

    public abstract boolean isCollided(BoundingBox others);
}
