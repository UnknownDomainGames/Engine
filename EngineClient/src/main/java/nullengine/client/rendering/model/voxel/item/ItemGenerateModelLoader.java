package nullengine.client.rendering.model.voxel.item;

import com.google.gson.JsonObject;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.voxel.Model;
import nullengine.client.rendering.model.voxel.ModelLoader;

import java.util.function.Function;

public final class ItemGenerateModelLoader implements ModelLoader {

    @Override
    public boolean isAccepts(AssetURL url, JsonObject json) {
        return json.has("parent") && "item/generate".equals(json.get("parent").getAsString());
    }

    @Override
    public Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter) {
        return null;
    }
}
