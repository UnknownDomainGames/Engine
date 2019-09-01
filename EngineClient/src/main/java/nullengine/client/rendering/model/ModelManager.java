package nullengine.client.rendering.model;

import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.rendering.model.data.ModelData;
import nullengine.client.rendering.model.item.ItemGenerationModelBaker;

import javax.annotation.Nonnull;
import java.util.*;

import static java.lang.String.format;

public class ModelManager implements AssetProvider<BakedModel> {

    private final Set<Asset<BakedModel>> registeredModels = new HashSet<>();
    private final Map<AssetURL, ModelData> loadedModelData = new HashMap<>();

    private final List<ModelBaker> modelBakers;

    public ModelManager() {
        modelBakers = List.of(new ItemGenerationModelBaker(), new DefaultModelBaker());
    }

    @Override
    public void init(AssetManager manager, AssetType<BakedModel> type) {
        manager.getReloadManager().addBefore("ReloadModels", "Texture", this::reloadModels);
        manager.getReloadManager().addAfter("BakeModels", "Texture", this::bakeModels);
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

    public ModelData getModelData(AssetURL url) {
        return null;
    }

    public BakedModel bakeModelData(ModelData modelData) {
        for (ModelBaker modelBaker : modelBakers) {
            if (modelBaker.isAccepts(modelData)) {
                return modelBaker.bake(modelData);
            }
        }
        throw new AssetLoadException(format("Cannot bake model: %s", modelData.url));
    }

    private void reloadModels() {
        loadedModelData.clear();
    }

    private void bakeModels() {
        registeredModels.forEach(Asset::reload);
        loadedModelData.clear();
    }

    @Override
    public void dispose() {

    }
}
