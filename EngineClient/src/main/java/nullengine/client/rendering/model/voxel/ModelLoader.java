package nullengine.client.rendering.model.voxel;

import com.google.gson.JsonObject;
import nullengine.client.asset.AssetURL;

import java.util.function.Function;

public interface ModelLoader {

    boolean isAccepts(AssetURL url, JsonObject json);

    Model load(AssetURL url, JsonObject json, Function<AssetURL, Model> modelGetter);
}
