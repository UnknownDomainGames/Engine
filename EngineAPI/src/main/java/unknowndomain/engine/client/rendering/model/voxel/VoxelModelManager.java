package unknowndomain.engine.client.rendering.model.voxel;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.asset.exception.AssetNotFoundException;
import unknowndomain.engine.client.rendering.texture.TextureAtlas;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static unknowndomain.engine.client.rendering.texture.StandardTextureAtlas.BLOCK;

public class VoxelModelManager {

    private final AssetManager assetManager;
    private final TextureAtlas blockAtlas;
    private final ModelLoader modelLoader;
    private final ModelBaker modelBaker;

    private final Set<AssetPath> registeredModels = new LinkedHashSet<>();
    private final Map<AssetPath, ModelData> modelDataMap = new HashMap<>();
    private final Map<AssetPath, MutableValue<Model>> modelMap = new HashMap<>();

    public VoxelModelManager(EngineClient engineClient) {
        this.assetManager = engineClient.getAssetManager();
        this.blockAtlas = engineClient.getRenderContext().getTextureManager().getTextureAtlas(BLOCK);
        this.modelLoader = new ModelLoader(this);
        this.modelBaker = new ModelBaker();
    }

    public ObservableValue<Model> registerModel(AssetPath path) {
        registeredModels.add(path);
        return modelMap.computeIfAbsent(path, $ -> new SimpleMutableObjectValue<>());
    }

    ModelData getModel(AssetPath path) {
        ModelData modelData = modelDataMap.get(path);
        if (modelData == null) {
            modelData = loadModel(path);
            modelDataMap.put(path, modelData);
        }
        return modelData;
    }

    private ModelData loadModel(AssetPath assetPath) {
        Optional<Path> path = assetManager.getPath(assetPath);
        if (path.isEmpty())
            throw new AssetNotFoundException(assetPath);
        try {
            return modelLoader.load(path.get());
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model.", e);
        }
    }

    public void reloadModelData() {
        modelDataMap.clear();
        for (AssetPath assetPath : registeredModels) {
            modelDataMap.put(assetPath, resolveTexture(loadModel(assetPath)));
        }
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

    public void bake() {
        for (Map.Entry<AssetPath, MutableValue<Model>> entry : modelMap.entrySet()) {
            entry.getValue().setValue(modelBaker.bake(modelDataMap.get(entry.getKey())));
        }
        modelDataMap.clear();
    }
}
