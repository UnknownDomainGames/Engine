package nullengine.client.rendering.model.block;

import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.model.ModelUtils;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.Direction;
import org.joml.Vector3fc;

public class BlockModel implements BakedModel {

    private final Mesh[] meshes;
    private final boolean[] fullFaces;

    public BlockModel(Mesh[] meshes, boolean[] fullFaces) {
        this.meshes = meshes;
        this.fullFaces = fullFaces;
    }

    @Override
    public void putVertexes(GLBuffer buffer, int coveredFace) {
        for (var mesh : meshes) {
            if (ModelUtils.checkCullFace(coveredFace, mesh.cullFaces)) {
                continue;
            }

            for (var vertex : mesh.vertexes) {
                buffer.pos(vertex.pos).color(1, 1, 1, 1).uv(vertex.u, vertex.v).normal(vertex.normal).endVertex();
            }
        }
    }

    @Override
    public boolean isFullFace(Direction direction) {
        return fullFaces[direction.index];
    }

    public static class Mesh {
        public final Vertex[] vertexes;
        public final byte cullFaces;

        public Mesh(Vertex[] vertexes, byte cullFaces) {
            this.vertexes = vertexes;
            this.cullFaces = cullFaces;
        }
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

}
