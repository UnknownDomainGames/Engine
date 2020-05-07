package engine.graphics.backend;

import engine.graphics.mesh.InstancedMesh;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureCubeMap;

public interface ResourceFactory {

    Texture2D.Builder createTexture2DBuilder();

    Texture2D getWhiteTexture2D();

    TextureCubeMap.Builder createTextureCubeMapBuilder();

    Sampler createSampler();

    MultiBufMesh.Builder createMultiBufMeshBuilder();

    SingleBufMesh.Builder createSingleBufMeshBuilder();

    <E> InstancedMesh.Builder<E> createInstancedMeshBuilder();
}
