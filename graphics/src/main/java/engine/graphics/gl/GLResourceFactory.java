package engine.graphics.gl;

import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.management.ResourceFactory;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.Texture2D;

public final class GLResourceFactory implements ResourceFactory {
    private final Thread renderingThread;

    public GLResourceFactory(Thread renderingThread) {
        this.renderingThread = renderingThread;
    }

    @Override
    public Texture2D.Builder createTexture2DBuilder() {
        return GLTexture2D.builder();
    }

    @Override
    public SingleBufferMesh.Builder createSingleBufferMeshBuilder() {
        return GLSingleBufferMesh.builder();
    }
}
