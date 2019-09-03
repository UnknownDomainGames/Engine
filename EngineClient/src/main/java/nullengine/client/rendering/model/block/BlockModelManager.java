package nullengine.client.rendering.model.block;

import nullengine.client.EngineClient;
import nullengine.client.asset.*;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.texture.TextureAtlas;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlockModelManager implements AssetProvider<BakedModel> {

    private final TextureAtlas blockAtlas;

    private final Map<AssetURL, ModelData> modelDataMap = new HashMap<>();
    private final List<Asset<BakedModel>> modelAssets = new LinkedList<>();

    private ModelLoader modelLoader;
    private ModelBaker modelBaker;

    public BlockModelManager(EngineClient engineClient) {
        this.blockAtlas = engineClient.getRenderContext().getTextureManager().getTextureAtlas(StandardTextureAtlas.DEFAULT);
    }

    @Override
    public void init(AssetManager manager, AssetType<BakedModel> type) {
        this.modelLoader = new ModelLoader(this, manager.getSourceManager(), type);
        this.modelBaker = new ModelBaker();
        manager.getReloadManager().addListener(
                new AssetReloadListener().name("ReloadModel").befores("Texture", "BakeModel").runnable(this::reloadModelData));
        manager.getReloadManager().addListener(
                new AssetReloadListener().name("BakeModel").befores("CleanTextureCache").afters("Texture").runnable(this::reload));
    }

    @Override
    public void register(Asset<BakedModel> asset) {
        modelAssets.add(asset);
    }

    @Override
    public void unregister(Asset<BakedModel> asset) {
        modelAssets.remove(asset);
    }

    private void reload() {
        modelAssets.forEach(Asset::reload);
    }

    @Override
    public void dispose() {

    }

    @Nonnull
    @Override
    public BakedModel loadDirect(AssetURL url) {
        return modelBaker.bake(getModelData(url));
    }

    ModelData getModelData(AssetURL url) {
        ModelData modelData = modelDataMap.get(url);
        if (modelData == null) {
            modelData = modelLoader.load(url);
            modelDataMap.put(url, modelData);
        }
        return modelData;
    }

    private void reloadModelData() {
        modelDataMap.clear();
        modelAssets.forEach(asset -> modelDataMap.put(asset.getPath(), resolveTexture(modelLoader.load(asset.getPath()))));
    }

    private ModelData resolveTexture(ModelData modelData) {
        for (var cube : modelData.cubes) {
            for (var face : cube.faces) {
                face.resolvedTexture = blockAtlas.addTexture(AssetURL.fromString(modelData.url, resolveTexture(face.texture, modelData.textures)));
            }
        }
        return modelData;
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        if (texture.charAt(0) == '$') {
            texture = textures.getOrDefault(texture.substring(1), texture);
        }
        return texture;
    }
}
