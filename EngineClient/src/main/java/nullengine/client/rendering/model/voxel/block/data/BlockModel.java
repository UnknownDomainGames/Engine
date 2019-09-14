package nullengine.client.rendering.model.voxel.block.data;

import com.google.gson.JsonElement;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.ModelUtils;
import nullengine.client.rendering.model.voxel.Model;
import nullengine.client.rendering.texture.TextureAtlasPart;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.Math2;
import nullengine.util.Direction;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static nullengine.client.rendering.model.voxel.block.data.ModelJsonUtils.array;
import static nullengine.client.rendering.model.voxel.block.data.ModelJsonUtils.map;
import static nullengine.util.JsonUtils.getAsStringOrNull;

public final class BlockModel implements Model {

    transient AssetURL url;
    String parent;
    transient BlockModel resolvedParent;
    Map<String, AssetURL> textures;
    transient List<AssetURL> requestTextures;
    Cube[] cubes;
    boolean[] fullFaces;

    public static BlockModel deserialize(AssetURL url, JsonElement json, Function<AssetURL, Model> modelGetter) {
        var object = json.getAsJsonObject();
        var data = new BlockModel();
        data.url = url;
        data.parent = getAsStringOrNull(object.get("parent"));
        resolveParent(data, modelGetter);
        data.textures = map(object.get("textures"), jsonElement -> AssetURL.fromString(url, jsonElement.getAsString()));
        Set<AssetURL> requestTextures = new HashSet<>();
        data.cubes = array(object.get("cubes"), Cube.class, element -> Cube.deserialize(data, element, requestTextures));
        resolveTextures(data, requestTextures);
        data.fullFaces = new boolean[6];
        if (data.resolvedParent != null) {
            System.arraycopy(data.resolvedParent.fullFaces, 0, data.fullFaces, 0, 6);
        }
        var fullFaces = object.getAsJsonArray("fullFaces");
        if (fullFaces != null) {
            for (var fullFace : fullFaces) {
                data.fullFaces[Direction.valueOf(fullFace.getAsString().toUpperCase()).index] = true;
            }
        }
        return data;
    }

    private static void resolveParent(BlockModel blockModel, Function<AssetURL, Model> modelGetter) {
        if (isNullOrEmpty(blockModel.parent)) {
            return;
        }

        var parentUrl = AssetURL.fromString(blockModel.url, blockModel.parent);
        var parentInstance = modelGetter.apply(parentUrl);
        if (parentInstance instanceof BlockModel) {
            blockModel.resolvedParent = (BlockModel) parentInstance;
        }
    }

    private static void resolveTextures(BlockModel blockModel, Set<AssetURL> requestTextures) {
        Map<String, AssetURL> textures = new HashMap<>();
        blockModel.textures.forEach((key, value) -> textures.put(key, resolveTexture(value, textures)));
        var parent = blockModel.resolvedParent;
        if (parent != null) {
            parent.textures.forEach((key, value) -> textures.put(key, resolveTexture(value, textures)));
        }
        blockModel.textures = Map.copyOf(textures);
        Set<AssetURL> resolvedRequestTextures = new HashSet<>();
        if (parent != null) {
            for (AssetURL requestTexture : parent.requestTextures) {
                resolvedRequestTextures.add(resolveTexture(requestTexture, textures));
            }
        }
        for (AssetURL requestTexture : requestTextures) {
            resolvedRequestTextures.add(resolveTexture(requestTexture, textures));
        }
        blockModel.requestTextures = List.copyOf(resolvedRequestTextures);
    }

    static AssetURL resolveTexture(AssetURL texture, Map<String, AssetURL> textures) {
        return isResolvedTexture(texture) ? texture : textures.getOrDefault(texture.getLocation().substring(1), texture);
    }

    static boolean isResolvedTexture(AssetURL texture) {
        return texture.getLocation().charAt(0) != '$';
    }

    @Override
    public nullengine.client.rendering.model.BakedModel bake(Function<AssetURL, TextureAtlasPart> textureGetter) {
        Map<Integer, List<float[]>> vertexes = new HashMap<>();
        bakeModel(this, vertexes, textureGetter);
        return new BakedModel(Map.copyOf(vertexes), fullFaces);
    }

    @Override
    public List<AssetURL> getTextures() {
        return requestTextures.stream().filter(BlockModel::isResolvedTexture).collect(Collectors.toList());
    }

    private void bakeModel(BlockModel data, Map<Integer, List<float[]>> vertexes, Function<AssetURL, TextureAtlasPart> textureGetter) {
        if (data.resolvedParent != null) {
            bakeParentModel(data, data.resolvedParent, vertexes, textureGetter);
        }

        for (var cube : data.cubes) {
            var faces = cube.faces;
            for (Direction direction : Direction.values()) {
                var face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(data, cube, face, direction, vertexes.computeIfAbsent(face.cullFaces, key -> new ArrayList<>()), textureGetter);
            }
        }
    }

    private void bakeParentModel(BlockModel bakingModel, BlockModel parent, Map<Integer, List<float[]>> vertexes, Function<AssetURL, TextureAtlasPart> textureGetter) {
        if (parent.resolvedParent != null) {
            bakeParentModel(bakingModel, parent.resolvedParent, vertexes, textureGetter);
        }

        for (var cube : parent.cubes) {
            var faces = cube.faces;
            for (Direction direction : Direction.values()) {
                var face = faces[direction.index];
                if (face == null)
                    continue;

                bakeFace(bakingModel, cube, face, direction, vertexes.computeIfAbsent(face.cullFaces, key -> new ArrayList<>()), textureGetter);
            }
        }
    }

    private void bakeFace(BlockModel bakingModel, Cube cube, Face face, Direction direction, List<float[]> mesh, Function<AssetURL, TextureAtlasPart> textureGetter) {
        var textureAtlasPart = textureGetter.apply(resolveTexture(face.texture.name, bakingModel.textures));
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
        var normal = Math2.calcNormalByVertices(v1, v2, v3);
        var normal1 = Math2.calcNormalByVertices(v1, v3, v4);

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{v2.x(), v2.y(), v2.z(), maxUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 2
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y(), normal.z()}); // 3

        vertexes.add(new float[]{v1.x(), v1.y(), v1.z(), minUv.x(), maxUv.y(), normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{v3.x(), v3.y(), v3.z(), maxUv.x(), minUv.y(), normal.x(), normal.y(), normal.z()}); // 3
        vertexes.add(new float[]{v4.x(), v4.y(), v4.z(), minUv.x(), minUv.y(), normal1.x(), normal1.y(), normal1.z()}); // 4
    }

    private static final class BakedModel implements nullengine.client.rendering.model.BakedModel {

        private final Map<Integer, List<float[]>> vertexes;
        private final boolean[] fullFaces;

        BakedModel(Map<Integer, List<float[]>> vertexes, boolean[] fullFaces) {
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
