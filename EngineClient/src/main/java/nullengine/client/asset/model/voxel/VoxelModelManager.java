package nullengine.client.asset.model.voxel;

import nullengine.client.EngineClient;
import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.exception.AssetNotFoundException;
import nullengine.client.asset.reloading.AssetReloadScheduler;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.texture.TextureAtlas;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class VoxelModelManager implements AssetProvider<VoxelModel> {

    private final TextureAtlas blockAtlas;
    private final ModelLoader modelLoader;
    private final ModelBaker modelBaker;

    private final Map<AssetPath, ModelData> modelDataMap = new HashMap<>();

    private AssetSourceManager sourceManager;

    private final List<Asset<VoxelModel>> modelAssets = new LinkedList<>();

    public VoxelModelManager(EngineClient engineClient) {
        this.blockAtlas = engineClient.getRenderContext().getTextureManager().getTextureAtlas(StandardTextureAtlas.BLOCK);
        this.modelLoader = new ModelLoader(this);
        this.modelBaker = new ModelBaker();
    }

    @Override
    public void init(AssetManager manager, AssetType<VoxelModel> type) {
        this.sourceManager = manager.getSourceManager();
        manager.getReloadManager().addBefore("VoxelModelData", "Texture", this::reloadModelData);
    }

    @Override
    public void register(Asset<VoxelModel> asset) {
        modelAssets.add(asset);
    }

    @Override
    public void unregister(Asset<VoxelModel> asset) {
        modelAssets.remove(asset);
    }

    @Override
    public void reload(AssetReloadScheduler scheduler) {
        modelAssets.forEach(asset -> scheduler.execute(asset::reload));
    }

    @Override
    public void dispose() {

    }

    @Nonnull
    @Override
    public VoxelModel loadDirect(AssetPath path) {
        return modelBaker.bake(getModelData(path));
    }

    ModelData getModelData(AssetPath path) {
        ModelData modelData = modelDataMap.get(path);
        if (modelData == null) {
            modelData = loadModelData(path);
            modelDataMap.put(path, modelData);
        }
        return modelData;
    }

    private ModelData loadModelData(AssetPath assetPath) {
        Optional<Path> path = sourceManager.getPath(assetPath);
        if (path.isEmpty())
            throw new AssetNotFoundException(assetPath);
        try {
            return modelLoader.load(path.get());
        } catch (IOException e) {
            throw new AssetLoadException("Cannot loadDirect model.", e);
        }
    }

    private void reloadModelData() {
        modelDataMap.clear();
        modelAssets.forEach(asset -> modelDataMap.put(asset.getPath(), resolveTexture(loadModelData(asset.getPath()))));
    }

    private ModelData resolveTexture(ModelData modelData) {
        for (ModelData.Element element : modelData.elements) {
            ModelData.Element.Cube cube = (ModelData.Element.Cube) element;
            for (ModelData.Element.Cube.Face face : cube.faces) {
                face.texture = resolveTexture(face.texture, modelData.textures);
                face._texture = blockAtlas.addTexture(AssetPath.of(face.texture));
            }
        }
        return modelData;
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        while (texture.charAt(0) == '$') {
            texture = textures.get(texture.substring(1));
        }
        return texture;
    }
}
