package engine.graphics.model.voxel;

import com.google.gson.JsonObject;
import engine.client.asset.AssetURL;

import java.util.function.Function;

public interface ModelLoader {

    boolean isAccepts(AssetURL url, JsonObject json);

    Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter);
}
