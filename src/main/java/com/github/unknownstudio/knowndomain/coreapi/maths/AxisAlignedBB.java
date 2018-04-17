package com.github.unknownstudio.knowndomain.coreapi.maths;

public class AxisAlignedBB extends BoundingBox {

    public Vector3d min, max;

    public AxisAlignedBB(double minX, double minY, double minZ,double maxX, double maxY, double maxZ){
        this(new Vector3d(minX,minY,minZ),new Vector3d(maxX,maxY,maxZ));
    }

    public AxisAlignedBB(Vector3d min, Vector3d max){
        this.min = min;
        this.max = max;
    }

    @Override
    public Vector3d getCentre() {
        return new Vector3d((min.getX() + max.getX()) / 2,(min.getY() + max.getY()) / 2,(min.getZ() + max.getZ()) / 2);
    }

    @Override
    public boolean isCollided(BoundingBox others) {
        return false; //TODO: collision checking
    }
}
