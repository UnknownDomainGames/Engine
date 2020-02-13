package engine.graphics.management;

import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.*;

public interface ResourceFactory {

    FrameBuffer getDefaultFrameBuffer();

    FrameBuffer.Builder createFrameBufferBuilder();

    Texture2D.Builder createTexture2DBuilder();

    RenderBuffer createRenderBuffer(TextureFormat format, int width, int height);

    RenderBuffer createRenderBuffer(TextureFormat format, Sampler sampler, int width, int height);

    RenderBuffer.Builder createRenderBufferBuilder();

    Mesh.Builder createMeshBuilder();

    SingleBufferMesh.Builder createSingleBufferMeshBuilder();
}
