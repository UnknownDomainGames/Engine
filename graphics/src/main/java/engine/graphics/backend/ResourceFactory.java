package engine.graphics.backend;

import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;

public interface ResourceFactory {

    Texture2D.Builder createTexture2DBuilder();

    Sampler createSampler();

    MultiBufMesh.Builder createMultiBufMeshBuilder();

    SingleBufMesh.Builder createSingleBufMeshBuilder();
}
