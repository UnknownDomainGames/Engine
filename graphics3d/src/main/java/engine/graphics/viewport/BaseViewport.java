package engine.graphics.viewport;

import engine.graphics.GraphicsEngine;
import engine.graphics.Scene3D;
import engine.graphics.camera.Camera;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.internal.graph.Scene3DRenderGraphHelper;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public abstract class BaseViewport implements Viewport {

    private int width;
    private int height;

    private Scene3D scene;

    private Camera camera = new Camera();

    protected final Matrix4f projectionMatrix = new Matrix4f();
    protected final Matrix4f projectionViewMatrix = new Matrix4f();

    protected final FrustumIntersection frustum = new FrustumIntersection();

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
        if (this.width == width && this.height == height) return;
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
    public boolean isShowing() {
        return renderGraph != null;
    }

    @Override
    public RenderGraph getRenderGraph() {
        return renderGraph;
    }

    @Override
    public RenderGraph show() {
        return show(Scene3DRenderGraphHelper.createRenderGraph(this));
    }

    @Override
    public RenderGraph show(RenderGraphInfo renderGraph) {
        if (isShowing()) throw new IllegalStateException("Viewport has been shown");
        this.renderGraph = GraphicsEngine.getGraphicsBackend().loadRenderGraph(renderGraph);
        return this.renderGraph;
    }

    @Override
    public synchronized void hide() {
        if (renderGraph == null) return;
        GraphicsEngine.getGraphicsBackend().removeRenderGraph(renderGraph);
        renderGraph = null;
    }
}
