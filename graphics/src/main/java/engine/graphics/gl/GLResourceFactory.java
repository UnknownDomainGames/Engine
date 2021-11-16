package engine.graphics.gl;

import engine.graphics.backend.ResourceFactory;
import engine.graphics.gl.mesh.GLInstancedMesh;
import engine.graphics.gl.mesh.GLMultiBufMesh;
import engine.graphics.gl.mesh.GLSingleBufMesh;
import engine.graphics.gl.texture.GLSampler;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.gl.texture.GLTextureCubeMap;
import engine.graphics.mesh.InstancedMesh;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureCubeMap;
import engine.graphics.util.BufferUtils;

final class GLResourceFactory implements ResourceFactory {
    private final Thread renderingThread;

    private final Texture2D whiteTexture2D;

    public GLResourceFactory(Thread renderingThread) {
        this.renderingThread = renderingThread;
        this.whiteTexture2D = GLTexture2D.of(BufferUtils.wrapAsByteBuffer(0xffffffff), 1, 1);
    }

    @Override
    public Texture2D.Builder createTexture2DBuilder() {
        return GLTexture2D.builder();
    }

    @Override
    public Texture2D getWhiteTexture2D() {
        return whiteTexture2D;
    }

    @Override
    public TextureCubeMap.Builder createTextureCubeMapBuilder() {
        return GLTextureCubeMap.builder();
    }

    @Override
    public Sampler.Builder createSamplerBuilder() {
        return GLSampler.builder();
    }

    @Override
    public MultiBufMesh.Builder createMultiBufMeshBuilder() {
        return GLMultiBufMesh.builder();
    }

    @Override
    public SingleBufMesh.Builder createSingleBufMeshBuilder() {
        return GLSingleBufMesh.builder();
    }

    @Override
    public <E> InstancedMesh.Builder<E> createInstancedMeshBuilder() {
        return new GLInstancedMesh.Builder<>();
    }
}
