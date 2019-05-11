package unknowndomain.engine.client.rendering.model.voxel;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.asset.AssetPath;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VoxelModelManager {

    private final List<AssetPath> modelPaths = new LinkedList<>();
    private final Map<AssetPath, Model> modelMap = new HashMap<>();
    private final Map<AssetPath, MutableValue<BakedModel>> bakedModelMap = new HashMap<>();

    public ObservableValue<BakedModel> registerModel(AssetPath path) {
        modelPaths.add(path);
        MutableValue<BakedModel> value = new SimpleMutableObjectValue<>();
        bakedModelMap.put(path, value);
        return value;
    }

    public ObservableValue<BakedModel> getBakedModel(AssetPath path) {
        return bakedModelMap.get(path);
    }

    public Model getModel(AssetPath path) {
        return modelMap.get(path);
    }
}
