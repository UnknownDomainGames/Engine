package engine.graphics.model.voxel.item;

import com.google.gson.JsonObject;
import engine.client.asset.AssetURL;
import engine.graphics.model.voxel.Model;
import engine.graphics.model.voxel.ModelLoader;
import engine.util.JsonUtils;

import java.util.function.Function;

import static engine.graphics.model.voxel.ModelJsonUtils.transformations;

public final class ItemGenerateModelLoader implements ModelLoader {

    @Override
    public boolean isAccepts(AssetURL url, JsonObject json) {
        return json.has("Parent") && "item/generate".equals(json.get("Parent").getAsString());
    }

    @Override
    public Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter) {
        ItemGenerateModel model = new ItemGenerateModel();
        model.url = url;
        model.texture = AssetURL.fromString(url, JsonUtils.getAsStringOrNull(json.get("Texture")));
        model.transforms = transformations(json.get("Display"));
        return model;
    }
}
