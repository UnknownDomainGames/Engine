package engine.graphics.internal.graph;

import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.light.LightManager;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.util.Struct;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.nio.ByteBuffer;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private final Matrix4f tempMatrix4f = new Matrix4f();

    private UniformBlock uniformMatrices;
    private UniformBlock uniformLight;
    private UniformTexture uniformTexture;

    private static class Matrices implements Struct {
        private Matrix4fc projMatrix;
        private Matrix4fc viewMatrix;
        private Matrix4fc modelMatrix;

        public Matrices(Matrix4fc projMatrix, Matrix4fc viewMatrix, Matrix4fc modelMatrix) {
            this.projMatrix = projMatrix;
            this.viewMatrix = viewMatrix;
            this.modelMatrix = modelMatrix;
        }

        @Override
        public int sizeof() {
            return 192;
        }

        @Override
        public ByteBuffer get(int index, ByteBuffer buffer) {
            projMatrix.get(index, buffer);
            viewMatrix.get(index + 64, buffer);
            modelMatrix.get(index + 128, buffer);
            return buffer;
        }
    }

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        this.uniformMatrices = resource.getUniformBlock("Matrices");
        this.uniformLight = resource.getUniformBlock("Light");
        this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        Scene3D scene = viewport.getScene();
        ShaderResource resource = drawer.getShaderResource();
        FrustumIntersection frustum = viewport.getFrustum();
        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(lightManager);
        scene.getRenderQueue().getGeometryList(RenderType.OPAQUE).stream()
                .filter(geometry -> geometry != null && geometry.getBoundingVolume().test(frustum))
                .forEach(geometry -> {
                    uniformMatrices.set(new Matrices( // TODO: optimize it
                            viewport.getProjectionMatrix(),
                            viewport.getViewMatrix(),
                            geometry.getWorldTransform().getTransformMatrix(tempMatrix4f)));
                    uniformTexture.set(geometry.getTexture());
                    resource.refresh();
                    renderer.drawMesh(geometry.getMesh());
                });
    }
}
