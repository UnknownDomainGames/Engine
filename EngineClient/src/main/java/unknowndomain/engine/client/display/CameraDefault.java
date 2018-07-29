package unknowndomain.engine.client.display;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.api.client.display.Camera;

public class CameraDefault implements Camera {

    public static final Vector3f UP_VECTOR = new Vector3f(0, 1, 0);
    private Vector3f pos = new Vector3f();
    private float yaw, pitch, roll;
    private double aX,aY,aZ;
    private double zoomRate;

    public CameraDefault(){
        zoomRate = 1;
    }

    @Override
    public void move(float x, float y, float z) {
        pos.add(x,y,z);
    }

    public void move(float x, float y, float z, boolean applyRotation){
        if(applyRotation){
            Matrix4f mat = new Matrix4f().translate(x,y,z).rotateY(yaw).rotateX(pitch);
            mat.transformPosition(pos);
        }
        else{
            pos.add(x,y,z);
        }
    }

    @Override
    public void moveTo(float x, float y, float z) {
        pos = new Vector3f(x,y,z);
    }

    @Override
    public void handleMove(int key, int action) {
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT){
            float moveC = 0.05f;
            Vector3f tmp = new Vector3f();
            switch (key){
                case GLFW.GLFW_KEY_W:
                    getFrontVector().mul(moveC, tmp);
                    pos.add(tmp);
                    break;
                case GLFW.GLFW_KEY_S:
                    getFrontVector().mul(moveC, tmp);
                    pos.sub(tmp);
                    break;
                case GLFW.GLFW_KEY_A:
                    getFrontVector().cross(UP_VECTOR, tmp);
                    tmp.mul(moveC);
                    pos.sub(tmp);
                    break;
                case GLFW.GLFW_KEY_D:
                    getFrontVector().cross(UP_VECTOR, tmp);
                    tmp.mul(moveC);
                    pos.add(tmp);
                    break;
                case GLFW.GLFW_KEY_SPACE:
                    move(0,1 * moveC,0);
                    break;
                case GLFW.GLFW_KEY_LEFT_SHIFT: case GLFW.GLFW_KEY_RIGHT_SHIFT:
                    move(0,-1 * moveC,0);
                    break;
                case GLFW.GLFW_KEY_Q:
                    roll -= SENSIBILITY * 10;
                    break;
                case GLFW.GLFW_KEY_E:
                    roll += SENSIBILITY * 10;
                    break;
            }
        }
    }

    private double lastX, lastY;
    private boolean setupLast = false;

    private static final float SENSIBILITY = 0.05f;

    @Override
    public void rotate(float x, float y) {
        double yaw = (x - lastX) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        if(setupLast){
            this.pitch += pitch;
            this.pitch = Math.min(89.0f, Math.max(-89.0f, this.pitch));
            this.yaw += yaw;
        }
        else setupLast = true;
    }

    @Override
    public void rotateTo(float yaw, float pitch) {
        this.pitch = pitch;
        this.pitch = Math.min(89.0f, Math.max(-89.0f, this.pitch));
        this.yaw = yaw;
    }

    @Override
    public void rotate(double angle, double fx, double fy, double fz) {
        aX += angle * fx;
        aY += angle * fy;
        aZ += angle * fz;
        aX %= 360.0;
        aY %= 360.0;
        aZ %= 360.0;
    }

    @Override
    public void rotateTo(double angle, double fx, double fy, double fz) {
        aX = angle * fx;
        aY = angle * fy;
        aZ = angle * fz;
        aX %= 360.0;
        aY %= 360.0;
        aZ %= 360.0;
    }

    @Override
    public void zoom(double ratio) {
        zoomRate *= ratio;
    }

    @Override
    public void zoomTo(double ratio) {
        zoomRate = ratio;
    }

    @Override
    public Matrix4f makeViewMatrix() {
//        Matrix4f mat = new Matrix4f();
//        Matrix4f matroll = new Matrix4f().identity();
//        matroll.rotate((float) Math.toRadians(roll), 0,0,1).transpose();
//        Matrix4f matyaw = new Matrix4f().identity();
//        matyaw.rotate((float) Math.toRadians(yaw), 0,1,0).transpose();
//        Matrix4f matpitch = new Matrix4f().identity();
//        matpitch.rotate((float) Math.toRadians(pitch), 1,0,0).transpose();
//
//        Matrix4f rotate = new Matrix4f();
//
//        matpitch.mul(matyaw, rotate);
//        matroll.mul(rotate, rotate);
//
//        Matrix4f translate = new Matrix4f().identity();
//        Vector3f p1 = new Vector3f();
//        pos.negate(p1);
//        translate.translate(p1);
//
//        rotate.mul(translate, mat);
//        return mat;
        Vector3fc front = getFrontVector();
        Vector3f center = new Vector3f();
        pos.add(front, center);
        Vector3f up = new Vector3f(0,1,0);
        //up.mulDirection(mat);
        return new Matrix4f().lookAt(pos,center, up).rotateZ((float)Math.toRadians(roll));
    }

    private Vector3fc getFrontVector() {
        return new Vector3f((float)(Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))), (float)Math.sin(Math.toRadians(pitch)), (float)(Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)))).normalize();
    }

    @Override
    public Matrix4f makeProjectionMatrix(float width, float height) {
        return new Matrix4f().perspective((float)(Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f * zoomRate)))), width / height, 0.01f, 1000f);
    }

	@Override
	public Vector3d getPosition() {
		return new Vector3d(pos.x,pos.y,pos.z);
	}

	@Override
	public Vector3d getRotation() {
		return new Vector3d(0,0,0);//TODO BUG
	}
}
