package engine.graphics.internal.graph;

import engine.graphics.Scene3D;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.texture.GLTextureBuffer;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.light.LightManager;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformImage;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Texture2D;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;

import java.util.stream.Stream;

public class ViewportTransparentDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private final Matrix4f tempMatrix4f = new Matrix4f();

    private UniformBlock uniformMatrices;
    private UniformBlock uniformLight;
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
        this.uniformMatrices = resource.getUniformBlock("Matrices");
        this.uniformLight = resource.getUniformBlock("Light");
        this.uniformTexture = resource.getUniformTexture("u_Texture");
        listHeader = resource.getUniformImage("linkedListHeader");
        uniformListBuffer = resource.getUniformImage("linkedListBuffer");
        uniformListBuffer.getBinding().setCanRead(false);
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        Scene3D scene = viewport.getScene();
        ShaderResource resource = drawer.getShaderResource();
        FrustumIntersection frustum = viewport.getFrustum();
        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(lightManager);
        if (headerImage != null) {
            listHeader.set(headerImage);
        }
        if (linkedListBuffer != null) {
            uniformListBuffer.set(linkedListBuffer);
        }
        if (atomicCounter != null) {
            GL33.glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCounter.getId());
        }
        Stream.concat(
                scene.getRenderQueue().getGeometryList(RenderType.TRANSPARENT).stream(),
                scene.getRenderQueue().getGeometryList(RenderType.TRANSLUCENT).stream())
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
