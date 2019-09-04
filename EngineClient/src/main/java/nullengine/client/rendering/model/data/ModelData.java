package nullengine.client.rendering.model.data;

import com.google.gson.JsonElement;
import nullengine.client.asset.AssetURL;
import nullengine.util.Direction;

import java.util.List;
import java.util.Map;

import static nullengine.client.rendering.model.data.ModelJsonUtils.array;
import static nullengine.client.rendering.model.data.ModelJsonUtils.map;

public final class ModelData {

    public AssetURL url;
    public String parent;
    public ModelData parentInstance;
    public Map<String, String> textures;
    public List<Texture> textureInstances;
    public Cube[] cubes;
    public boolean[] fullFaces;

    public static ModelData deserialize(AssetURL url, JsonElement json) {
        var object = json.getAsJsonObject();
        var data = new ModelData();
        data.url = url;
        data.parent = object.get("parent").getAsString();
        data.textures = map(object.get("textures"), JsonElement::getAsString);
        data.cubes = array(object.get("cubes"), Cube.class, element -> Cube.deserialize(data, element));
        data.fullFaces = new boolean[6];
        var fullFaces = object.getAsJsonArray("fullFaces");
        if (fullFaces != null) {
            for (var fullFace : fullFaces) {
                data.fullFaces[Direction.valueOf(fullFace.getAsString().toUpperCase()).index] = true;
            }
        }
        return data;
    }
}
