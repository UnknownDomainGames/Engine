package nullengine.client.rendering.model.voxel.block;

import com.google.gson.JsonObject;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.voxel.Model;
import nullengine.client.rendering.model.voxel.ModelLoader;
import nullengine.client.rendering.model.voxel.block.data.BlockModel;

import java.util.function.Function;

public final class BlockModelLoader implements ModelLoader {

    @Override
    public boolean isAccepts(AssetURL url, JsonObject json) {
        return true;
    }

    @Override
    public Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter) {
        return BlockModel.deserialize(url, json, modelGetter);
    }
}
