package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.camera.FixedCamera;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.callback.FramebufferSizeCallback;
import nullengine.util.Color;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class PerspectiveViewPort implements ViewPort {

    private int width;
    private int height;

    private Scene scene;

    private Color clearColor = new Color(0f, 0f, 0f, 1f);
    private boolean isClearColor;
    private boolean isClearDepth;
    private boolean isClearStencil;

    private float fovAngle = 60;
    private float aspect;
    private float zNear = 0.01f;
    private float zFar = 1000f;

    private Camera camera = new FixedCamera();

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

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public Color getClearColor() {
        return clearColor;
    }

    @Override
    public void setClearColor(Color clearColor) {
        this.clearColor = clearColor;
    }

    @Override
    public boolean isClearColor() {
        return isClearColor;
    }

    @Override
    public void setClearColor(boolean clearColor) {
        isClearColor = clearColor;
    }

    @Override
    public boolean isClearDepth() {
        return isClearDepth;
    }

    @Override
    public void setClearDepth(boolean clearDepth) {
        isClearDepth = clearDepth;
    }

    @Override
    public boolean isClearStencil() {
        return isClearStencil;
    }

    @Override
    public void setClearStencil(boolean clearStencil) {
        isClearStencil = clearStencil;
    }

    @Override
    public void setClearMask(boolean color, boolean depth, boolean stencil) {
        setClearColor(color);
        setClearDepth(depth);
        setClearStencil(stencil);
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

    private Window window;
    private FramebufferSizeCallback framebufferSizeCallback;

    @Override
    public void bindWindow(Window window) {
        if (this.window != null) this.window.removeFramebufferSizeCallback(framebufferSizeCallback);
        if (window != null) {
            if (framebufferSizeCallback == null) {
                framebufferSizeCallback = (_window, width, height) -> setSize(width, height);
            }
            setSize(window.getWidth(), window.getHeight());
            window.addFramebufferSizeCallback(framebufferSizeCallback);
        }
        this.window = window;
    }

    @Override
    public void unbindWindow() {
        bindWindow(null);
    }
}
