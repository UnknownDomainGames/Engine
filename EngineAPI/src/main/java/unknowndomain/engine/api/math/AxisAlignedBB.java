package unknowndomain.engine.api.math;

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
        if (others instanceof AxisAlignedBB){
            Vector3d[] pat = new Vector3d[]{((AxisAlignedBB) others).min, ((AxisAlignedBB) others).max};
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    for (int z = 0; z < 2; z++) {
                        if(min.getX() <= pat[x].getX() && pat[x].getX() <= max.getX() &&
                                min.getY() <= pat[y].getY() && pat[y].getY() <= max.getY() &&
                                min.getZ() <= pat[z].getZ() && pat[z].getZ() <= max.getZ())
                            return true; // if any vertex of other bounding box is inside this bounding box, it must be collided
                    }
                }
            } // iterate every vertex of other bounding box
        }
        return false;
    }
}
