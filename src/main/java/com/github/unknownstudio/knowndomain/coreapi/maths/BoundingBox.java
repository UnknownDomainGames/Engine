package com.github.unknownstudio.knowndomain.coreapi.maths;

public abstract class BoundingBox {

    public abstract Vector3d getCentre();

    public abstract boolean isCollided(BoundingBox others);

}
