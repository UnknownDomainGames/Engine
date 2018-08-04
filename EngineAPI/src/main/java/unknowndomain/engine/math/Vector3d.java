package unknowndomain.engine.math;

public class Vector3d {
    private double x,y,z;

    public Vector3d(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3d add(Vector3d others){
        this.x += others.x;
        this.y += others.y;
        this.z += others.z;
        return this;
    }

    public Vector3d minus(Vector3d others){
        this.x -= others.x;
        this.y -= others.y;
        this.z -= others.z;
        return this;
    }

    public double dotProduct(Vector3d others){
        return x * others.x + y * others.y + z * others.z;
    }

    public Vector3d crossProduct(Vector3d others){
        return crossProduct(others, this);
    }
    public Vector3d crossProduct(Vector3d others, Vector3d dest){
        double x = this.y * others.z - this.z * others.y;
        double y = this.z * others.x - this.x * others.z;
        double z = this.x * others.y - this.y * others.x;
        dest.x = x;
        dest.y = y;
        dest.z = z;
        return dest;
    }
}
