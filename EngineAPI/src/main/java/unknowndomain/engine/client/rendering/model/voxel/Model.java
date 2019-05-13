package unknowndomain.engine.client.rendering.model.voxel;

import org.joml.Vector3fc;

import java.util.List;

class Model {

    private List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public static class Vertex {
        Vector3fc pos;
        float u, v;
        Vector3fc normal;

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
