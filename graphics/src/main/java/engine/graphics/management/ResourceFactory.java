package engine.graphics.management;

import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.Texture2D;

public interface ResourceFactory {

    Texture2D.Builder createTexture2DBuilder();

    Mesh.Builder createMeshBuilder();

    SingleBufferMesh.Builder createSingleBufferMeshBuilder();
}
