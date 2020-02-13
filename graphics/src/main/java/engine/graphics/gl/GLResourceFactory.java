package engine.graphics.gl;

import engine.graphics.gl.mesh.GLMesh;
import engine.graphics.gl.mesh.GLSingleBufferMesh;
import engine.graphics.gl.texture.GLRenderBuffer;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.management.ResourceFactory;
import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.RenderBuffer;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;

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
    public Mesh.Builder createMeshBuilder() {
        return GLMesh.builder();
    }

    @Override
    public SingleBufferMesh.Builder createSingleBufferMeshBuilder() {
        return GLSingleBufferMesh.builder();
    }

    @Override
    public RenderBuffer createRenderBuffer(TextureFormat format, int width, int height) {
        return new GLRenderBuffer(format, width, height);
    }

    @Override
    public RenderBuffer createRenderBuffer(TextureFormat format, Sampler sampler, int width, int height) {
        return new GLRenderBuffer(format, sampler, width, height);
    }

    @Override
    public RenderBuffer.Builder createRenderBufferBuilder() {
        return GLRenderBuffer.builder();
    }
}
