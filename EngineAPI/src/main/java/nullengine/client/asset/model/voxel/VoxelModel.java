package nullengine.client.asset.model.voxel;

import org.joml.Vector3fc;

import java.util.List;

public class VoxelModel {

    private List<Mesh> meshes;

    public VoxelModel(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public static class Vertex {
        public Vector3fc pos;
        public float u, v;
        public Vector3fc normal;

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
