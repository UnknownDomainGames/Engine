package unknowndomain.engine.client.asset.model.voxel;

import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.asset.*;
import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.asset.exception.AssetNotFoundException;
import unknowndomain.engine.client.asset.source.AssetSourceManager;
import unknowndomain.engine.client.rendering.texture.TextureAtlas;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static unknowndomain.engine.client.rendering.texture.StandardTextureAtlas.BLOCK;

public class VoxelModelManager implements AssetProvider<VoxelModel> {

    private final TextureAtlas blockAtlas;
    private final ModelLoader modelLoader;
    private final ModelBaker modelBaker;

    private final Map<AssetPath, ModelData> modelDataMap = new HashMap<>();

    private AssetSourceManager sourceManager;

    private final List<Asset<VoxelModel>> models = new LinkedList<>();

    public VoxelModelManager(EngineClient engineClient) {
        this.blockAtlas = engineClient.getRenderContext().getTextureManager().getTextureAtlas(BLOCK);
        this.modelLoader = new ModelLoader(this);
        this.modelBaker = new ModelBaker();
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
            throw new AssetLoadException("Cannot load model.", e);
        }
    }

    private void reloadModelData() {
        modelDataMap.clear();
        models.forEach(asset -> modelDataMap.put(asset.getPath(), resolveTexture(loadModelData(asset.getPath()))));
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

    private void bake() {
        models.forEach(Asset::reload);
    }

    @Override
    public void init(AssetManager manager, AssetType<VoxelModel> type) {
        this.sourceManager = manager.getSourceManager();
        manager.getReloadDispatcher().addBefore("VoxelModelReload", "Texture", this::reloadModelData);
        manager.getReloadDispatcher().addAfter("VoxelModelBake", "Texture", this::bake);
    }

    @Override
    public void register(Asset<VoxelModel> asset) {
        models.add(asset);
    }

    @Override
    public void dispose(Asset<VoxelModel> asset) {
        // Don't need do anything.
    }

    @Override
    public void dispose() {

    }

    @Nonnull
    @Override
    public VoxelModel load(AssetPath path) {
        return modelBaker.bake(getModelData(path));
    }
}
