package com.github.unknownstudio.unknowndomain.engineapi.math;

public abstract class BoundingBox {

    public abstract Vector3d getCentre();

    public abstract boolean isCollided(BoundingBox others);

}
