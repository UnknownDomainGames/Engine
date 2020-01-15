package nullengine.client.rendering3d.viewport;

public final class PerspectiveViewport extends BaseViewport {

    private float fovAngle = 60;
    private float aspect;
    private float zNear = 0.01f;
    private float zFar = 1000f;

    public float getFovAngle() {
        return fovAngle;
    }

    public void setFovAngle(float fovAngle) {
        this.fovAngle = fovAngle;
        updateProjectionMatrix();
    }

    public float getAspect() {
        return aspect;
    }

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
        updateProjectionMatrix();
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
        updateProjectionMatrix();
    }

    protected void onFrameSizeChanged() {
        aspect = (float) getWidth() / getHeight();
        updateProjectionMatrix();
    }

    protected void updateProjectionMatrix() {
        projectionMatrix.setPerspective((float) Math.toRadians(fovAngle), aspect, zNear, zFar);
        updateProjectionViewMatrix();
    }

    protected void updateProjectionViewMatrix() {
        projectionViewMatrix.set(projectionMatrix).mul(getCamera().getViewMatrix());
        frustum.set(projectionViewMatrix);
    }
}
