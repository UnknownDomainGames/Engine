package nullengine.client.asset.model.block;

import nullengine.client.rendering.texture.TextureAtlasPart;
import nullengine.math.Math2;
import nullengine.util.Facing;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ModelBaker {

    public ModelBaker() {
    }

    public BlockModel bake(ModelData modelData) {
        BakedModelPrimer primer = new BakedModelPrimer();
        primer.fullFaces = modelData.fullFaces;
        List<ModelData.Element> elements = modelData.elements;
        for (ModelData.Element element : elements) {
            ModelData.Element.Cube cube = (ModelData.Element.Cube) element;

            ModelData.Element.Cube.Face[] faces = cube.faces;
            for (Facing facing : Facing.values()) {
                ModelData.Element.Cube.Face face = faces[facing.index];
                if (face == null)
                    continue;

                bakeFace(cube, face, facing, primer.getVertexes(face.cullFaces));
            }

        }
        return primer.build();
    }

    private void bakeFace(ModelData.Element.Cube cube, ModelData.Element.Cube.Face face, Facing facing, List<BlockModel.Vertex> mesh) {
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

        switch (facing) {
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
                throw new IllegalStateException("Unexpected value: " + facing);
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

        public List<Pair<boolean[], List<BlockModel.Vertex>>> vertexesList = new ArrayList<>();
        public boolean[] fullFaces;

        public List<BlockModel.Vertex> getVertexes(boolean[] cullFaces) {
            for (Pair<boolean[], List<BlockModel.Vertex>> mesh : vertexesList) {
                if (Arrays.equals(mesh.getLeft(), cullFaces)) {
                    return mesh.getRight();
                }
            }
            List<BlockModel.Vertex> mesh = new ArrayList<>();
            vertexesList.add(Pair.of(cullFaces, mesh));
            return mesh;
        }

        public BlockModel build() {
            List<BlockModel.Mesh> bakedMeshes = new ArrayList<>();
            for (Pair<boolean[], List<BlockModel.Vertex>> mesh : vertexesList) {
                bakedMeshes.add(new BlockModel.Mesh(mesh.getRight().toArray(new BlockModel.Vertex[0]), mesh.getLeft()));
            }
            return new BlockModel(List.copyOf(bakedMeshes), fullFaces);
        }
    }
}
