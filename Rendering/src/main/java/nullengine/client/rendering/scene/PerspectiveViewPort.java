package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class PerspectiveViewPort implements ViewPort {

    private int width;
    private int height;

    private float fovAngle = 60;
    private float aspect;
    private float zNear = 0.01f;
    private float zFar = Float.MAX_VALUE;

    private Camera camera;

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f projectionViewMatrix = new Matrix4f();

    private FrustumIntersection frustum = new FrustumIntersection();

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        onFrameSizeChanged();
    }

    private void onFrameSizeChanged() {
        aspect = (float) width / height;
        onProjectionChanged();
    }

    private void onProjectionChanged() {
        projectionMatrix.setPerspective((float) Math.toRadians(fovAngle),
                aspect, zNear, zFar);
        onProjectionViewChanged();
    }

    private void onProjectionViewChanged() {
        projectionViewMatrix.set(projectionMatrix).mul(camera.getViewMatrix());
        frustum.set(projectionViewMatrix);
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(Camera camera) {
        if (this.camera != null) {
            this.camera.setChangeListener(null);
        }
        this.camera = camera;
        if (camera != null) {
            camera.setChangeListener($ -> onProjectionViewChanged());
            onProjectionViewChanged();
        }
    }

    @Override
    public Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4fc getViewMatrix() {
        return camera.getViewMatrix();
    }

    @Override
    public Matrix4fc getProjectionViewMatrix() {
        return projectionViewMatrix;
    }

    @Override
    public FrustumIntersection getFrustum() {
        return frustum;
    }

    public float getFovAngle() {
        return fovAngle;
    }

    public void setFovAngle(float fovAngle) {
        this.fovAngle = fovAngle;
        onProjectionChanged();
    }

    public float getAspect() {
        return aspect;
    }

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
        onProjectionChanged();
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
        onProjectionChanged();
    }
}
