package nullengine.client.rendering.model;

import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.rendering.model.block.data.BlockModel;
import nullengine.client.rendering.model.item.ItemGenerationModelBaker;

import javax.annotation.Nonnull;
import java.util.*;

import static java.lang.String.format;

public class ModelManager implements AssetProvider<BakedModel> {

    private final Set<Asset<BakedModel>> registeredModels = new HashSet<>();
    private final Map<AssetURL, Model> loadedModels = new HashMap<>();

    private final List<ModelBaker> modelBakers;

    public ModelManager() {
        modelBakers = List.of(new ItemGenerationModelBaker(), new DefaultModelBaker());
    }

    @Override
    public void init(AssetManager manager, AssetType<BakedModel> type) {
        manager.getReloadManager().addListener(
                new AssetReloadListener().name("ReloadModel").befores("Texture").runnable(this::reloadModels));
        manager.getReloadManager().addListener(
                new AssetReloadListener().name("BakeModel").befores("CleanTextureCache").afters("Texture").runnable(this::bakeModels));
    }

    @Override
    public void register(Asset<BakedModel> asset) {
        registeredModels.add(asset);
    }

    @Override
    public void unregister(Asset<BakedModel> asset) {
        registeredModels.remove(asset);
    }

    @Nonnull
    @Override
    public BakedModel loadDirect(AssetURL url) {
        return bakeModelData(getModelData(url));
    }

    public BlockModel getModelData(AssetURL url) {
        return null;
    }

    public BakedModel bakeModelData(BlockModel blockModel) {
        for (ModelBaker modelBaker : modelBakers) {
            if (modelBaker.isAccepts(blockModel)) {
                return modelBaker.bake(blockModel);
            }
        }
        throw new AssetLoadException(format("Cannot bake model: %s", blockModel.url));
    }

    private void reloadModels() {
        loadedModels.clear();
    }

    private void bakeModels() {
        registeredModels.forEach(Asset::reload);
        loadedModels.clear();
    }

    @Override
    public void dispose() {

    }
}
