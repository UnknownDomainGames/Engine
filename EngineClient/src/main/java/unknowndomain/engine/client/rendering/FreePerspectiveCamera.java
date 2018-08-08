package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public class FreePerspectiveCamera implements Camera.Perspective {
    private Vector3f pos = new Vector3f(0, 0, 0);
    private Vector3f up = new Vector3f(0, 1, 0);
    private Vector3f target = new Vector3f(1, 0, 0);

    private float movingFactor = 0.05f;
    private float fovy = (float) Math.toRadians(60.0f);
    private float aspect = 854 / 480;
    private float zNear = 0.1f;
    private float zFar = 512f;

    public float getFovy() {
        return fovy;
    }

    public void setFovy(float fovy) {
        this.fovy = fovy;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }

    public float getMovingFactor() {
        return movingFactor;
    }

    public void setMovingFactor(float movingFactor) {
        this.movingFactor = movingFactor;
    }

    public void forward() {
        Vector3f diff = target.sub(pos, new Vector3f()).mul(getMovingFactor());
        pos.add(diff);
        target.add(diff);
    }

    public void backward() {
        Vector3f diff = target.sub(pos, new Vector3f()).mul(getMovingFactor());
        pos.sub(diff);
        target.add(diff);
    }

    public void left() {
        Vector3f diff = target.sub(pos, new Vector3f()).cross(up);
        pos.add(diff);
        target.add(diff);
    }

    public void right() {
        Vector3f diff = target.sub(pos, new Vector3f()).cross(up);
        pos.sub(diff);
        target.sub(diff);
    }

    @Override
    public void rotate(float dx, float dy) {
        target = target.sub(pos, new Vector3f())
                .rotate(new Quaternionf().rotate(dx, dy, 0))
                .add(pos);
        System.out.println("rot");
        System.out.println(target);
    }

    @Override
    public void rotateTo(float x, float y) {
//        new Vector3f().rotate(new Quaternionf().rotationTo(target.sub(position, new Vector3f()),
//                new V))
    }

    @Override
    public Vector3f getPosition() {
        return null;
    }

    @Override
    public Vector3f getLookAt() {
        return null;
    }

    @Override
    public Vector3f getFrontVector() {
        return null;
    }

    public void move(float x, float y, float z) {
        pos.add(x, y, z);
    }

    public void moveTo(float x, float y, float z) {
        pos = new Vector3f(x, y, z);
    }

    public void lookAt(float x, float y, float z) {
        this.target.set(x, y, z);
    }

    public Matrix4f view() {
        return new Matrix4f().setLookAt(pos, target, up);
    }

    public Matrix4f projection() {
        return new Matrix4f().setPerspective(this.fovy, this.aspect, this.zNear, this.zFar);
    }
}
