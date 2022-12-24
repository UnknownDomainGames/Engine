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
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL42C;

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
        uniformMatrices = resource.getUniformBlock("Matrices", 192);
        uniformLight = resource.getUniformBlock("Light", 2096);
        uniformTexture = resource.getUniformTexture("u_Texture");
        listHeader = resource.getUniformImage("linkedListHeader", true, true);
        uniformListBuffer = resource.getUniformImage("linkedListBuffer", false, true);
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();

        Scene3D scene = viewport.getScene();

        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(0, lightManager);

        if (headerImage != null) {
            listHeader.setTexture(headerImage);
        }
        if (linkedListBuffer != null) {
            uniformListBuffer.setTexture(linkedListBuffer);
        }
        if (atomicCounter != null) {
            GL33C.glBindBufferBase(GL42C.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCounter.getId());
        }

        uniformMatrices.set(0, viewport.getProjectionMatrix());
        uniformMatrices.set(64, viewport.getViewMatrix());

        FrustumIntersection frustum = viewport.getFrustum();
        Stream.concat(
                        scene.getRenderQueue().getGeometryList(RenderType.TRANSPARENT).stream(),
                        scene.getRenderQueue().getGeometryList(RenderType.TRANSLUCENT).stream())
                .filter(geometry -> geometry.shouldRender(frustum))
                .forEach(geometry -> {
                    uniformMatrices.set(128, geometry.getWorldTransform().getTransformMatrix(tempMatrix4f));
                    uniformTexture.setTexture(geometry.getTexture());
                    renderer.drawMesh(geometry.getMesh());
                });
    }
}
