package engine.graphics.model.voxel.block;

import engine.client.asset.AssetURL;
import engine.graphics.math.RenderingMath;
import engine.graphics.math.Transform;
import engine.graphics.model.DisplayType;
import engine.graphics.model.ModelUtils;
import engine.graphics.model.voxel.Model;
import engine.graphics.texture.TextureAtlasRegion;
import engine.graphics.vertex.VertexDataBuffer;
import engine.util.Direction;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static engine.graphics.model.voxel.ModelLoadUtils.fillTransformationArray;

public final class BlockModel implements Model {

    transient AssetURL url;
    String parent;
    transient BlockModel resolvedParent;
    Map<String, AssetURL> textures;
    transient List<AssetURL> requestTextures;
    Cube[] cubes;
    boolean[] fullFaces;
    Transform[] transforms;

    @Override
    public engine.graphics.model.BakedModel bake(Function<AssetURL, TextureAtlasRegion> textureGetter) {
        Map<Integer, List<float[]>> vertexes = new HashMap<>();
        bakeModel(this, vertexes, textureGetter);
        fillTransformationArray(transforms);
        return new BakedModel(Map.copyOf(vertexes), fullFaces, transforms);
    }

    @Override
    public Collection<AssetURL> getTextures() {
        return requestTextures.stream().filter(BlockModelLoader::isResolvedTexture).collect(Collectors.toList());
    }

    private void bakeModel(BlockModel data, Map<Integer, List<float[]>> vertexes, Function<AssetURL, TextureAtlasRegion> textureGetter) {
        if (data.resolvedParent != null) {
            bakeParentModel(data, data.resolvedParent, vertexes, textureGetter);
        }

        for (var cube : data.cubes) {
            var faces = cube.faces;
            for (Direction direction : Direction.VALUES) {
                var face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(data, cube, face, direction, vertexes.computeIfAbsent(face.cullFaces, key -> new ArrayList<>()), textureGetter);
            }
        }
    }

    private void bakeParentModel(BlockModel bakingModel, BlockModel parent, Map<Integer, List<float[]>> vertexes, Function<AssetURL, TextureAtlasRegion> textureGetter) {
        if (parent.resolvedParent != null) {
            bakeParentModel(bakingModel, parent.resolvedParent, vertexes, textureGetter);
        }

        for (var cube : parent.cubes) {
            var faces = cube.faces;
            for (Direction direction : Direction.VALUES) {
                var face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(bakingModel, cube, face, direction, vertexes.computeIfAbsent(face.cullFaces, key -> new ArrayList<>()), textureGetter);
            }
        }
    }

    private void bakeFace(BlockModel bakingModel, Cube cube, Face face, Direction direction, List<float[]> mesh, Function<AssetURL, TextureAtlasRegion> textureGetter) {
        var textureAtlasPart = textureGetter.apply(BlockModelLoader.resolveTexture(face.texture.name, bakingModel.textures));
        var uv = face.texture.uv;
        var width = textureAtlasPart.getMaxU() - textureAtlasPart.getMinU();
        var height = textureAtlasPart.getMaxV() - textureAtlasPart.getMinV();
        var minU = textureAtlasPart.getMinU() + width * uv.x();
        var maxU = textureAtlasPart.getMinU() + width * uv.z();
        var minV = textureAtlasPart.getMinV() + height * uv.y();
        var maxV = textureAtlasPart.getMinV() + height * uv.w();
        var positions = cube.getFacePositions(direction);
        bakeQuad(mesh, positions[0], positions[1], positions[2], positions[3], new Vector2f(minU, minV), new Vector2f(maxU, maxV));
    }

    private void bakeQuad(List<float[]> vertexes, Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Vector2fc minUv, Vector2fc maxUv) {
        var normal = RenderingMath.calcNormalByVertices(v1, v2, v3);
        var normal1 = RenderingMath.calcNormalByVertices(v1, v3, v4);

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{v2.x(), v2.y(), v2.z(), maxUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 2
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y(), normal.z()}); // 3

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y(), normal.z()}); // 3
        vertexes.add(new float[]{v4.x(), v4.y(), v4.z(), minUv.x(), minUv.y(), normal1.x(), normal1.y(), normal1.z()}); // 4
    }

    private static final class BakedModel implements engine.graphics.model.BakedModel {

        private final Map<Integer, List<float[]>> vertexes;
        private final boolean[] fullFaces;
        private final Transform[] transforms;

        BakedModel(Map<Integer, List<float[]>> vertexes, boolean[] fullFaces, Transform[] transforms) {
            this.vertexes = vertexes;
            this.fullFaces = fullFaces;
            this.transforms = transforms;
        }

        @Override
        public void putVertexes(VertexDataBuffer buffer, int coveredFace) {
            for (var vertexes : this.vertexes.entrySet()) {
                if (ModelUtils.checkCullFace(coveredFace, vertexes.getKey())) {
                    continue;
                }

                for (var vertex : vertexes.getValue()) {
                    buffer.pos(vertex, 0).rgba(1, 1, 1, 1).tex(vertex, 3).normal(vertex, 5).endVertex();
                }
            }
        }

        @Override
        public boolean isFullFace(Direction direction) {
            return fullFaces[direction.index];
        }

        @Override
        public Transform getTransformation(DisplayType type) {
            return transforms[type.ordinal()];
        }
    }
}
