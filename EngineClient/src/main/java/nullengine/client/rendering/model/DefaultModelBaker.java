package nullengine.client.rendering.model;

import nullengine.client.rendering.model.data.Cube;
import nullengine.client.rendering.model.data.Face;
import nullengine.client.rendering.model.data.ModelData;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.Math2;
import nullengine.util.Direction;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultModelBaker implements ModelBaker {
    @Override
    public boolean isAccepts(ModelData data) {
        return true;
    }

    @Override
    public BakedModel bake(ModelData data) {
        Map<Integer, List<float[]>> vertexes = new HashMap<>();
        for (var cube : data.cubes) {
            var faces = cube.faces;
            for (Direction direction : Direction.values()) {
                var face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(cube, face, direction, vertexes.computeIfAbsent(face.cullFaces, key -> new ArrayList<>()));
            }
        }
        return new Model(Map.copyOf(vertexes), data.fullFaces);
    }

    private void bakeFace(Cube cube, Face face, Direction direction, List<float[]> mesh) {
        var textureAtlasPart = face.textureInstance;
        var u = textureAtlasPart.getMaxU() - textureAtlasPart.getMinU();
        var v = textureAtlasPart.getMaxV() - textureAtlasPart.getMinV();
        var minU = textureAtlasPart.getMinU() + u * face.uv.x();
        var maxU = textureAtlasPart.getMinU() + u * face.uv.z();
        var minV = textureAtlasPart.getMinV() + v * face.uv.y();
        var maxV = textureAtlasPart.getMinV() + v * face.uv.w();
        var positions = cube.getFacePositions(direction);
        bakeQuad(mesh, positions[0], positions[1], positions[2], positions[3], new Vector2f(minU, minV), new Vector2f(maxU, maxV));
    }

    private void bakeQuad(List<float[]> vertexes, Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Vector2fc minUv, Vector2fc maxUv) {
        var normal = Math2.calcNormalByVertices(v1, v2, v3);
        var normal1 = Math2.calcNormalByVertices(v1, v3, v4);

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y()}); // 1
        vertexes.add(new float[]{v2.x(), v2.y(), v2.z(), maxUv.x(), maxUv.y(), normal.x(), normal.y()}); // 2
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y()}); // 3

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y()}); // 1
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y()}); // 3
        vertexes.add(new float[]{v4.x(), v4.y(), v4.z(), minUv.x(), minUv.y(), normal1.x(), normal1.y()}); // 4
    }

    private static final class Model implements BakedModel {

        private final Map<Integer, List<float[]>> vertexes;
        private final boolean[] fullFaces;

        Model(Map<Integer, List<float[]>> vertexes, boolean[] fullFaces) {
            this.vertexes = vertexes;
            this.fullFaces = fullFaces;
        }

        @Override
        public void putVertexes(GLBuffer buffer, int coveredFace) {
            for (var vertexes : this.vertexes.entrySet()) {
                if (ModelUtils.checkCullFace(coveredFace, vertexes.getKey())) {
                    continue;
                }

                for (var vertex : vertexes.getValue()) {
                    buffer.pos(vertex, 0).color(1, 1, 1, 1).uv(vertex, 3).normal(vertex, 5).endVertex();
                }
            }
        }

        @Override
        public boolean isFullFace(Direction direction) {
            return fullFaces[direction.index];
        }
    }
}
