package engine.graphics.management;

import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.RenderBuffer;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;

public interface ResourceFactory {

    Texture2D.Builder createTexture2DBuilder();

    Mesh.Builder createMeshBuilder();

    SingleBufferMesh.Builder createSingleBufferMeshBuilder();

    RenderBuffer createRenderBuffer(TextureFormat format, int width, int height);

    RenderBuffer createRenderBuffer(TextureFormat format, Sampler sampler, int width, int height);

    RenderBuffer.Builder createRenderBufferBuilder();
}
