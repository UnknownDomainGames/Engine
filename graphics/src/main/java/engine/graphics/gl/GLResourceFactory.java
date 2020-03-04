package engine.graphics.gl;

import engine.graphics.backend.ResourceFactory;
import engine.graphics.gl.mesh.GLMultiBufMesh;
import engine.graphics.gl.mesh.GLSingleBufMesh;
import engine.graphics.gl.texture.GLSampler;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.texture.Sampler;
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
    public Sampler createSampler() {
        return new GLSampler();
    }

    @Override
    public MultiBufMesh.Builder createMultiBufMeshBuilder() {
        return GLMultiBufMesh.builder();
    }

    @Override
    public SingleBufMesh.Builder createSingleBufMeshBuilder() {
        return GLSingleBufMesh.builder();
    }
}
