package unknowndomain.engine.client.rendering.model.voxel;

import java.util.List;

public class BakedModel {

    private List<Mesh> meshes;

    public BakedModel(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public static class Mesh {
        public final float[] data;
        public final boolean[] cullFaces;

        public Mesh(float[] data, boolean[] cullFaces) {
            this.data = data;
            this.cullFaces = cullFaces;
        }
    }

}
