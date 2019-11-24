package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class PerspectiveViewPort implements ViewPort {

    private int width;
    private int height;

    private float fovyAngle = 60;
    private float aspect;
    private float zNear = 0.01f;
    private float zFar = 1000f;

    private Camera camera;

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f projectionViewMatrix = new Matrix4f();

    private FrustumIntersection frustum = new FrustumIntersection();

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
        onFrameSizeChanged();
    }

    private void onFrameSizeChanged() {
        aspect = (float) width / height;
        onProjectionChanged();
    }

    private void onProjectionChanged() {
        projectionMatrix.setPerspective((float) Math.toRadians(fovyAngle),
                aspect, zNear, zFar);
        onCameraChanged();
    }

    private void onCameraChanged() {
        projectionViewMatrix.set(projectionMatrix).mul(camera.getViewMatrix());
        frustum.set(projectionViewMatrix);
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        if (this.camera != null) {
            this.camera.setChangeListener(null);
        }
        this.camera = camera;
        if (camera != null) {
            camera.setChangeListener($ -> onCameraChanged());
            onCameraChanged();
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

    public float getFovyAngle() {
        return fovyAngle;
    }

    public void setFovyAngle(float fovyAngle) {
        this.fovyAngle = fovyAngle;
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
