package unknowndomain.engine.client.rendering.model.voxel;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import unknowndomain.engine.client.asset.AssetPath;

import java.util.HashMap;
import java.util.Map;

public class VoxelModelManager {

    private final Map<AssetPath, Model> modelMap = new HashMap<>();
    private final Map<AssetPath, MutableValue<BakedModel>> bakedModelMap = new HashMap<>();

}
