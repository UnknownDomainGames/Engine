package nullengine.client.rendering.model.block;

import com.google.gson.JsonObject;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.Model;
import nullengine.client.rendering.model.ModelLoader;
import nullengine.client.rendering.model.block.data.BlockModel;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.texture.TextureAtlas;
import nullengine.client.rendering.texture.TextureManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class BlockModelLoader implements ModelLoader {

    private final TextureAtlas textureAtlas;

    public BlockModelLoader() {
        this.textureAtlas = TextureManager.instance().getTextureAtlas(StandardTextureAtlas.DEFAULT);
    }

    private void resolveParent(BlockModel blockModel, Function<AssetURL, Model> modelGetter) {
        if (isNullOrEmpty(blockModel.parent)) {
            return;
        }

        var parentUrl = AssetURL.fromString(blockModel.url, blockModel.parent);
        var parentInstance = modelGetter.apply(parentUrl);
        if (parentInstance instanceof BlockModel) {
            blockModel.parentInstance = (BlockModel) parentInstance;
        }
    }

    private void resolveTextures(BlockModel blockModel) {
        var parent = blockModel.parentInstance;
        Map<String, String> resolvedTextures = new HashMap<>();
        if (parent != null) {
            resolvedTextures.putAll(parent.textures);
        }
        blockModel.textures.forEach((key, value) -> resolvedTextures.put(key, resolveTexture(value, resolvedTextures)));
        blockModel.textures = Map.copyOf(resolvedTextures);
        blockModel.textureInstances.forEach(texture -> texture.instance = textureAtlas.addTexture(
                AssetURL.fromString(blockModel.url,
                        resolveTexture(texture.name, resolvedTextures))));
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        if (texture.charAt(0) == '$') {
            texture = textures.getOrDefault(texture.substring(1), texture);
        }
        return texture;
    }

    @Override
    public boolean isAccepts(AssetURL url, JsonObject json) {
        return true;
    }

    @Override
    public Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter) {
        var modelData = BlockModel.deserialize(url, json);
        resolveParent(modelData, modelGetter);
        resolveTextures(modelData);
        return modelData;
    }
}
