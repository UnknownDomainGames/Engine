package unknowndomain.engine.client.block.model;
import org.joml.Vector3d;

import unknowndomain.engine.api.math.BlockPos;
/** 
* @author byxiaobai
* 来自教程
*/


public class GameItem {

    private final Mesh mesh;
    
    private final Vector3d position;
    
    private float scale;

    private final Vector3d rotation;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3d(0, 0, 0);
        scale = 1;
        rotation = new Vector3d(0, 0, 0);
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(double x, double y, double z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    
    public void setPosition(BlockPos loc) {
        this.position.x = loc.getX();
        this.position.y = loc.getY();
        this.position.z = loc.getZ();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
}