package nullengine.client.rendering.model.voxel;

import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.exception.AssetNotFoundException;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.model.voxel.block.BlockModelLoader;
import nullengine.client.rendering.model.voxel.item.ItemGenerateModelLoader;
import nullengine.client.rendering.texture.TextureAtlas;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.util.JsonUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ModelManager implements AssetProvider<BakedModel> {

    private final Set<Asset<BakedModel>> registeredModels = new HashSet<>();
    private final Map<AssetURL, Model> loadedModels = new HashMap<>();

    private final List<ModelLoader> loaders;

    private AssetSourceManager source;
    private AssetType<BakedModel> type;
    private TextureAtlas textureAtlas;

    public ModelManager() {
        loaders = List.of(new ItemGenerateModelLoader(), new BlockModelLoader());
    }

    @Override
    public void init(AssetManager manager, AssetType<BakedModel> type) {
        this.source = manager.getSourceManager();
        this.type = type;
        this.textureAtlas = TextureManager.instance().getDefaultAtlas();
        manager.getReloadManager().addListener(
                AssetReloadListener.builder().name("ReloadVoxelModel").before("Texture").runnable(this::reloadModels).build());
        manager.getReloadManager().addListener(
                AssetReloadListener.builder().name("BakeVoxelModel").before("CleanTextureCache").after("Texture").runnable(this::bakeModels).build());
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
        return getModel(url).bake(textureAtlas::getTexture);
    }

    private Model getModel(AssetURL url) {
        Model model = loadedModels.get(url);
        if (model == null) {
            model = loadModel(url);
            loadedModels.put(url, model);
        }
        return model;
    }

    private Model loadModel(AssetURL url) {
        var path = source.getPath(url.toFileLocation(type)).orElseThrow(() -> new AssetNotFoundException(url.toFileLocation(type)));
        try (var reader = Files.newBufferedReader(path)) {
            var json = JsonUtils.parser().parse(reader).getAsJsonObject();
            for (var loader : loaders) {
                if (loader.isAccepts(url, json)) {
                    var model = loader.load(url, json, this::getModel);
                    model.getTextures().forEach(textureAtlas::addTexture);
                    return model;
                }
            }
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model file", e);
        }
        throw new AssetLoadException("Cannot find model loader");
    }

    private void reloadModels() {
        loadedModels.clear();
        registeredModels.forEach(asset -> getModel(asset.getUrl()));
    }

    private void bakeModels() {
        registeredModels.forEach(Asset::reload);
        loadedModels.clear();
    }

    @Override
    public void dispose() {
    }
}
