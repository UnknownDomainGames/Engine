package engine.graphics.viewport;

import engine.graphics.GraphicsEngine;
import engine.graphics.Scene3D;
import engine.graphics.camera.Camera;
import engine.graphics.display.Window;
import engine.graphics.display.callback.WindowSizeCallback;
import engine.graphics.graph.RenderGraph;
import engine.graphics.internal.Platform3D;
import engine.graphics.internal.impl.Scene3DRenderGraphHelper;
import engine.graphics.texture.FrameBuffer;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class BaseViewport implements Viewport {

    private int width;
    private int height;

    private Scene3D scene;

    private Camera camera = new Camera();

    protected final Matrix4f projectionMatrix = new Matrix4f();
    protected final Matrix4f projectionViewMatrix = new Matrix4f();

    protected final FrustumIntersection frustum = new FrustumIntersection();

    private boolean showing;

    private FrameBuffer frameBuffer;

    private Window window;
    private WindowSizeCallback windowSizeCallback;

    private RenderGraph renderGraph;

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
    public Scene3D getScene() {
        return scene;
    }

    @Override
    public void setScene(Scene3D scene) {
        this.scene = scene;
    }

    protected abstract void onFrameSizeChanged();

    protected abstract void updateProjectionMatrix();

    protected abstract void updateProjectionViewMatrix();

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
            camera.setChangeListener($ -> updateProjectionViewMatrix());
            updateProjectionViewMatrix();
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

    @Override
    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    @Override
    public Window getWindow() {
        return this.window;
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void show(FrameBuffer frameBuffer) {
        show(null, frameBuffer);
    }

    @Override
    public void show(Window window) {
        show(window, frameBuffer != null ? frameBuffer : Platform3D.getInstance().getDefaultFrameBuffer());
    }

    @Override
    public void show(Window window, FrameBuffer frameBuffer) {
        this.frameBuffer = notNull(frameBuffer, "framebuffer");
        if (showing) hide();
        if (window != null) {
            setSize(window.getWidth(), window.getHeight());
            if (windowSizeCallback == null) {
                windowSizeCallback = (_window, width, height) -> setSize(width, height);
            }
            window.addWindowSizeCallback(windowSizeCallback);
        }
        this.window = window;
        this.showing = true;
        Platform3D.getInstance().getViewportHelper().show(this);
        this.renderGraph = GraphicsEngine.getGraphicsBackend()
                .loadRenderGraph(Scene3DRenderGraphHelper.createRenderGraph(this));
        this.renderGraph.bindWindow(window);
    }

    @Override
    public void hide() {
        Platform3D.getInstance().getViewportHelper().hide(this);
        GraphicsEngine.getGraphicsBackend().removeRenderGraph(renderGraph);
        if (this.window != null) {
            this.window.removeWindowSizeCallback(windowSizeCallback);
        }
        this.window = null;
        this.showing = false;
    }
}
