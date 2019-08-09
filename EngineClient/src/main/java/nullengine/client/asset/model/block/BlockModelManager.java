package nullengine.client.asset.model.block;

import nullengine.client.EngineClient;
import nullengine.client.asset.*;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.texture.TextureAtlas;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlockModelManager implements AssetProvider<BlockModel> {

    private final TextureAtlas blockAtlas;

    private final Map<AssetURL, ModelData> modelDataMap = new HashMap<>();
    private final List<Asset<BlockModel>> modelAssets = new LinkedList<>();

    private ModelLoader modelLoader;
    private ModelBaker modelBaker;
    private AssetType<BlockModel> type;

    public BlockModelManager(EngineClient engineClient) {
        this.blockAtlas = engineClient.getRenderContext().getTextureManager().getTextureAtlas(StandardTextureAtlas.BLOCK);
    }

    @Override
    public void init(AssetManager manager, AssetType<BlockModel> type) {
        this.modelLoader = new ModelLoader(this, manager.getSourceManager(), type);
        this.modelBaker = new ModelBaker();
        this.type = type;
        manager.getReloadManager().addBefore("VoxelModelDataReload", "Texture", this::reloadModelData);
        manager.getReloadManager().addAfter("VoxelModelBake", "Texture", this::reload);
    }

    @Override
    public void register(Asset<BlockModel> asset) {
        modelAssets.add(asset);
    }

    @Override
    public void unregister(Asset<BlockModel> asset) {
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
    public BlockModel loadDirect(AssetURL url) {
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
        for (ModelData.Element element : modelData.elements) {
            ModelData.Element.Cube cube = (ModelData.Element.Cube) element;
            for (ModelData.Element.Cube.Face face : cube.faces) {
                face.texture = resolveTexture(face.texture, modelData.textures);
                face.resolvedTexture = blockAtlas.addTexture(AssetURL.fromString(modelData.url, face.texture));
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
