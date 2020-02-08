package engine.graphics.viewport;

public final class OrthoViewport extends BaseViewport {

    private float zNear = 0.01f;
    private float zFar = 1000f;

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
        updateProjectionMatrix();
    }

    protected void updateProjectionMatrix() {
        projectionMatrix.setOrtho(0, getWidth(), getHeight(), 0, zNear, zFar);
        updateProjectionViewMatrix();
    }

    protected void updateProjectionViewMatrix() {
        projectionViewMatrix.set(projectionMatrix).mul(getCamera().getViewMatrix());
        frustum.set(projectionViewMatrix);
    }
}
