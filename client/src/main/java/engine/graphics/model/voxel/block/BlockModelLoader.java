package engine.graphics.model.voxel.block;

import com.google.gson.JsonObject;
import engine.client.asset.AssetURL;
import engine.graphics.model.voxel.Model;
import engine.graphics.model.voxel.ModelJsonUtils;
import engine.graphics.model.voxel.ModelLoader;
import engine.util.Direction;

import java.util.*;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static engine.graphics.model.voxel.ModelJsonUtils.array;
import static engine.graphics.model.voxel.ModelJsonUtils.map;
import static engine.util.JsonUtils.getAsStringOrNull;

public final class BlockModelLoader implements ModelLoader {

    @Override
    public boolean isAccepts(AssetURL url, JsonObject json) {
        return true;
    }

    @Override
    public Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter) {
        var object = json.getAsJsonObject();
        var data = new BlockModel();
        data.url = url;
        data.parent = getAsStringOrNull(object.get("Parent"));
        resolveParent(data, modelGetter);
        data.textures = map(object.get("Textures"), jsonElement -> AssetURL.fromString(url, jsonElement.getAsString()));
        Set<AssetURL> requestTextures = new HashSet<>();
        data.cubes = array(object.get("Cubes"), Cube.class, element -> Cube.deserialize(data, element, requestTextures));
        resolveTextures(data, requestTextures);
        data.fullFaces = new boolean[6];
        if (data.resolvedParent != null) {
            System.arraycopy(data.resolvedParent.fullFaces, 0, data.fullFaces, 0, 6);
        }
        var fullFaces = object.getAsJsonArray("FullFaces");
        if (fullFaces != null) {
            for (var fullFace : fullFaces) {
                data.fullFaces[Direction.valueOf(fullFace.getAsString().toUpperCase()).index] = true;
            }
        }
        data.transforms = ModelJsonUtils.transformations(object.get("Display"));
        var parent = data.resolvedParent;
        if (parent != null) {
            for (int i = 0; i < data.transforms.length; i++) {
                if (data.transforms[i] == null && parent.transforms[i] != null) {
                    data.transforms[i] = parent.transforms[i];
                }
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
}
