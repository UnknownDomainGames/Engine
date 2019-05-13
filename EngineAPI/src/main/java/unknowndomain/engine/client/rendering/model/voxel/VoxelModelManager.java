package unknowndomain.engine.client.rendering.model.voxel;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.asset.exception.AssetNotFoundException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static unknowndomain.engine.client.rendering.texture.StandardTextureAtlas.BLOCK;

public class VoxelModelManager {

    private final AssetManager assetManager;
    private final ModelLoader modelLoader;
    private final ModelBaker modelBaker;

    private final Map<AssetPath, ModelData> modelDataMap = new HashMap<>();
    private final Map<AssetPath, MutableValue<Model>> modelMap = new HashMap<>();

    public VoxelModelManager(EngineClient engineClient) {
        this.assetManager = engineClient.getAssetManager();
        this.modelLoader = new ModelLoader(engineClient.getRenderContext().getTextureManager().getTextureAtlas(BLOCK));
        this.modelBaker = new ModelBaker(this);
    }

    public ObservableValue<Model> registerModel(AssetPath path) {
        MutableValue<Model> value = new SimpleMutableObjectValue<>();
        modelMap.put(path, value);
        return value;
    }

    public ObservableValue<Model> getBakedModel(AssetPath path) {
        return modelMap.get(path);
    }

    ModelData getModel(AssetPath path) {
        return modelDataMap.computeIfAbsent(path, this::loadModel);
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

    public void reload() {
        modelDataMap.clear();
        for (Map.Entry<AssetPath, MutableValue<Model>> entry : modelMap.entrySet()) {
            entry.getValue().setValue(modelBaker.bake(loadModel(entry.getKey())));
        }
    }
}
