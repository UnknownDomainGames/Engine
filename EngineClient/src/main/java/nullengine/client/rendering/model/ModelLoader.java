package nullengine.client.rendering.model;

import nullengine.client.asset.AssetType;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.model.data.ModelData;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.texture.TextureAtlas;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.util.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class ModelLoader {

    private final TextureAtlas textureAtlas;
    private final ModelManager modelManager;

    public ModelLoader(ModelManager modelManager) {
        this.modelManager = modelManager;
        this.textureAtlas = TextureManager.instance().getTextureAtlas(StandardTextureAtlas.DEFAULT);
    }

    public ModelData load(AssetSourceManager sourceManager, AssetType<?> type, AssetURL url) {
        return load(url, sourceManager.getPath(url.toFileLocation(type))
                .orElseThrow(() -> new AssetLoadException("Asset is not found. URL: " + url)));
    }

    private ModelData load(AssetURL url, Path path) {
        try (var reader = Files.newBufferedReader(path)) {
            var modelData = ModelData.deserialize(url, JsonUtils.DEFAULT_JSON_PARSER.parse(reader));
            resolveParent(modelData);
            resolveTextures(modelData);
            return modelData;
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model. URL: " + url, e);
        }
    }

    private void resolveParent(ModelData modelData) {
        if (isNullOrEmpty(modelData.parent)) {
            return;
        }

        var parentUrl = AssetURL.fromString(modelData.url, modelData.parent);
        modelData.parentInstance = modelManager.getModelData(parentUrl);
    }

    private void resolveTextures(ModelData modelData) {
        var parent = modelData.parentInstance;
        Map<String, String> resolvedTextures = new HashMap<>();
        if (parent != null) {
            resolvedTextures.putAll(parent.textures);
        }
        modelData.textures.forEach((key, value) -> resolvedTextures.put(key, resolveTexture(value, resolvedTextures)));
        modelData.textures = Map.copyOf(resolvedTextures);
        modelData.textureInstances.forEach(texture -> texture.instance = textureAtlas.addTexture(
                AssetURL.fromString(modelData.url,
                        resolveTexture(texture.name, resolvedTextures))));
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        if (texture.charAt(0) == '$') {
            texture = textures.getOrDefault(texture.substring(1), texture);
        }
        return texture;
    }
}
