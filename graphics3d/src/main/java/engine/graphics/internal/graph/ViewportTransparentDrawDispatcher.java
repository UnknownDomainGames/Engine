package engine.graphics.internal.graph;

import engine.graphics.Geometry;
import engine.graphics.Scene3D;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.texture.GLTextureBuffer;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.*;
import engine.graphics.texture.Texture2D;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL42C;

public class ViewportTransparentDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private final Matrix4f tempMatrix4f = new Matrix4f();

    private UniformBlock uniformLight;
    private Uniform uniformProjMatrix;
    private Uniform uniformViewMatrix;
    private Uniform uniformViewModelMatrix;
    private UniformTexture uniformTexture;

    private UniformImage uniformListBuffer;
    private UniformImage listHeader;
    private Texture2D headerImage;
    private GLTextureBuffer linkedListBuffer;
    private GLVertexBuffer atomicCounter;

    public ViewportTransparentDrawDispatcher(Viewport viewport, Texture2D headerImage, GLTextureBuffer linkedListBuffer, GLVertexBuffer atomicCounter) {
        this.viewport = viewport;
        this.headerImage = headerImage;
        this.linkedListBuffer = linkedListBuffer;
        this.atomicCounter = atomicCounter;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        uniformLight = resource.getUniformBlock("Light", 2096);
        uniformProjMatrix = resource.getUniform("projMatrix");
        uniformViewMatrix = resource.getUniform("viewMatrix");
        uniformViewModelMatrix = resource.getUniform("viewModelMatrix");
        uniformTexture = resource.getUniformTexture("u_Texture");
        listHeader = resource.getUniformImage("linkedListHeader", true, true);
        uniformListBuffer = resource.getUniformImage("linkedListBuffer", false, true);
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();

        Scene3D scene = viewport.getScene();
        Matrix4fc viewMatrix = viewport.getViewMatrix();
        FrustumIntersection frustum = viewport.getFrustum();

        uniformLight.set(0, scene.getLightManager());

        if (headerImage != null) {
            listHeader.setTexture(headerImage);
        }
        if (linkedListBuffer != null) {
            uniformListBuffer.setTexture(linkedListBuffer);
        }
        if (atomicCounter != null) {
            GL33C.glBindBufferBase(GL42C.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCounter.getId());
        }

        uniformProjMatrix.set(viewport.getProjectionMatrix());
        uniformViewMatrix.set(viewMatrix);

        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.TRANSPARENT)) {
            if (geometry.shouldRender(frustum)) {
                uniformViewModelMatrix.set(viewMatrix.mul(geometry.getWorldTransform().getTransformMatrix(tempMatrix4f), tempMatrix4f));
                uniformTexture.setTexture(geometry.getTexture());
                renderer.drawMesh(geometry.getMesh());
            }
        }
        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.TRANSLUCENT)) {
            if (geometry.shouldRender(frustum)) {
                uniformViewModelMatrix.set(viewMatrix.mul(geometry.getWorldTransform().getTransformMatrix(tempMatrix4f), tempMatrix4f));
                uniformTexture.setTexture(geometry.getTexture());
                renderer.drawMesh(geometry.getMesh());
            }
        }
    }
}
