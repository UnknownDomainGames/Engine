package unknowndomain.engine.client.block.model;

/** 
* @author byxiaobai
* 坐标
*/
public class Location {
	private double x;
	private double y;
	private double z;
	public Location(double x,double y,double z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	public Location(Location loc) {
		this.setX(loc.x);
		this.setY(loc.y);
		this.setZ(loc.z);
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
	
	public void addX(double x) {
		this.x += x;
	}
	public void addY(double y) {
		this.y += y;
	}
	public void addZ(double z) {
		this.z += z;
	}
	public Location add(Location others){
        this.x += others.x;
        this.y += others.y;
        this.z += others.z;
        return this;
    }

    public Location minus(Location others){
        this.x -= others.x;
        this.y -= others.y;
        this.z -= others.z;
        return this;
    }

    public double dotProduct(Location others){
        return x * others.x + y * others.y + z * others.z;
    }

    public Location crossProduct(Location others){
        return crossProduct(others, this);
    }
    public Location crossProduct(Location others, Location dest){
        double x = this.y * others.z - this.z * others.y;
        double y = this.z * others.x - this.x * others.z;
        double z = this.x * others.y - this.y * others.x;
        dest.x = x;
        dest.y = y;
        dest.z = z;
        return dest;
    }
    public String toString() {
    	return "x: "+x+",y: "+y+",z: "+z;
    }
    public Location clone() {
    	return new Location(this.x,this.y,this.z);
    }
}
