package nullengine.client.rendering.model._block;

import nullengine.client.rendering.texture.TextureAtlasPart;
import nullengine.math.Math2;
import nullengine.util.Direction;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModelBaker {

    public ModelBaker() {
    }

    public BlockModel bake(ModelData modelData) {
        var primer = new BakedModelPrimer();
        primer.fullFaces = modelData.fullFaces;
        for (var cube : modelData.cubes) {
            ModelData.Cube.Face[] faces = cube.faces;
            for (Direction direction : Direction.values()) {
                ModelData.Cube.Face face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(cube, face, direction, primer.getVertexes(face.cullFaces));
            }

        }
        return primer.build();
    }

    private void bakeFace(ModelData.Cube cube, ModelData.Cube.Face face, Direction direction, List<BlockModel.Vertex> mesh) {
        TextureAtlasPart textureAtlasPart = face.resolvedTexture;
        float u = textureAtlasPart.getMaxU() - textureAtlasPart.getMinU();
        float v = textureAtlasPart.getMaxV() - textureAtlasPart.getMinV();
        float minU = textureAtlasPart.getMinU() + u * face.uv.x();
        float maxU = textureAtlasPart.getMinU() + u * face.uv.z();
        float minV = textureAtlasPart.getMinV() + v * face.uv.y();
        float maxV = textureAtlasPart.getMinV() + v * face.uv.w();
        Vector3fc from = cube.from;
        Vector3fc to = cube.to;

        Vector3f v1, v2, v3, v4;

        switch (direction) {
            case NORTH:
                v1 = new Vector3f(to.x(), from.y(), from.z());
                v2 = new Vector3f(from.x(), from.y(), from.z());
                v3 = new Vector3f(from.x(), to.y(), from.z());
                v4 = new Vector3f(to.x(), to.y(), from.z());
                break;
            case SOUTH:
                v1 = new Vector3f(from.x(), from.y(), to.z());
                v2 = new Vector3f(to.x(), from.y(), to.z());
                v3 = new Vector3f(to.x(), to.y(), to.z());
                v4 = new Vector3f(from.x(), to.y(), to.z());
                break;
            case EAST:
                v1 = new Vector3f(to.x(), from.y(), to.z());
                v2 = new Vector3f(to.x(), from.y(), from.z());
                v3 = new Vector3f(to.x(), to.y(), from.z());
                v4 = new Vector3f(to.x(), to.y(), to.z());
                break;
            case WEST:
                v1 = new Vector3f(from.x(), from.y(), from.z());
                v2 = new Vector3f(from.x(), from.y(), to.z());
                v3 = new Vector3f(from.x(), to.y(), to.z());
                v4 = new Vector3f(from.x(), to.y(), from.z());
                break;
            case UP:
                v1 = new Vector3f(from.x(), to.y(), to.z());
                v2 = new Vector3f(to.x(), to.y(), to.z());
                v3 = new Vector3f(to.x(), to.y(), from.z());
                v4 = new Vector3f(from.x(), to.y(), from.z());
                break;
            case DOWN:
                v1 = new Vector3f(to.x(), from.y(), to.z());
                v2 = new Vector3f(from.x(), from.y(), to.z());
                v3 = new Vector3f(from.x(), from.y(), from.z());
                v4 = new Vector3f(to.x(), from.y(), from.z());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
        bakeQuad(mesh, v1, v2, v3, v4, new Vector2f(minU, minV), new Vector2f(maxU, maxV));
    }

    private void bakeQuad(List<BlockModel.Vertex> mesh, Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Vector2fc minUv, Vector2fc maxUv) {
        var normal = Math2.calcNormalByVertices(v1, v2, v3);
        var normal1 = Math2.calcNormalByVertices(v1, v3, v4);
        mesh.add(new BlockModel.Vertex(v1, minUv.x(), maxUv.y(), normal)); // 1
        mesh.add(new BlockModel.Vertex(v2, maxUv.x(), maxUv.y(), normal)); // 2
        mesh.add(new BlockModel.Vertex(v3, maxUv.x(), minUv.y(), normal)); // 3

        mesh.add(new BlockModel.Vertex(v1, minUv.x(), maxUv.y(), normal)); // 1
        mesh.add(new BlockModel.Vertex(v3, maxUv.x(), minUv.y(), normal)); // 3
        mesh.add(new BlockModel.Vertex(v4, minUv.x(), minUv.y(), normal1)); // 4
    }

    private class BakedModelPrimer {

        public Map<Byte, List<BlockModel.Vertex>> vertexes = new HashMap<>();
        public boolean[] fullFaces;

        public List<BlockModel.Vertex> getVertexes(byte cullFaces) {
            return vertexes.computeIfAbsent(cullFaces, key -> new ArrayList<>());
        }

        public BlockModel build() {
            List<BlockModel.Mesh> bakedMeshes = new ArrayList<>();
            for (var mesh : vertexes.entrySet()) {
                bakedMeshes.add(new BlockModel.Mesh(mesh.getValue().toArray(new BlockModel.Vertex[0]), mesh.getKey()));
            }
            return new BlockModel(bakedMeshes.toArray(new BlockModel.Mesh[0]), fullFaces);
        }
    }
}
