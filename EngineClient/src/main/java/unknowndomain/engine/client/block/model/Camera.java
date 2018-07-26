package unknowndomain.engine.client.block.model;
import org.joml.Vector3d;
import unknowndomain.engine.api.math.BlockPos;

public class Camera {

    private final Vector3d position;

    private final Vector3d rotation;
    
    private Vector3d cameraInc;//临时量
    
    public Vector3d getCameraInc() {
    	return this.cameraInc;
    }
    
    public void setCameraInc(Vector3d cameraInc) {
    	this.cameraInc=cameraInc;
    }

    public Camera() {
        this.position = new Vector3d(0, 0, 0);
        this.rotation = new Vector3d(0, 0, 0);
        this.cameraInc = new Vector3d(0, 0, 0);
    }

    public Camera(Vector3d position, Vector3d rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3d getPosition() {
        return position;
    }
    
    public BlockPos getLocation() {
    	return new BlockPos((int)position.x,(int)position.y,(int)position.z);
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(double offsetX,double offsetY, double offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(double x, double y, double z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(double offsetX, double offsetY, double offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}