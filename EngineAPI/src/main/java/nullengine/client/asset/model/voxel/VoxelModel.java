package nullengine.client.asset.model.voxel;

import org.joml.Vector3fc;

import java.util.List;

public class VoxelModel {

    private final List<Mesh> meshes;
    private final boolean[] fullFaces;

    public VoxelModel(List<Mesh> meshes, boolean[] fullFaces) {
        this.meshes = meshes;
        this.fullFaces = fullFaces;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public boolean[] getFullFaces() {
        return fullFaces;
    }

    public static class Vertex {
        public final Vector3fc pos;
        public final float u, v;
        public final Vector3fc normal;

        public Vertex(Vector3fc pos, float u, float v, Vector3fc normal) {
            this.pos = pos;
            this.u = u;
            this.v = v;
            this.normal = normal;
        }
    }

    public static class Mesh {
        public final Vertex[] vertexes;
        public final boolean[] cullFaces;

        public Mesh(Vertex[] vertexes, boolean[] cullFaces) {
            this.vertexes = vertexes;
            this.cullFaces = cullFaces;
        }
    }

}
