package nullengine.client.rendering.model.block.data;

import com.google.gson.JsonElement;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.model.Model;
import nullengine.util.Direction;

import java.util.List;
import java.util.Map;

import static nullengine.client.rendering.model.block.data.ModelJsonUtils.array;
import static nullengine.client.rendering.model.block.data.ModelJsonUtils.map;

public final class BlockModel implements Model {

    public AssetURL url;
    public String parent;
    public BlockModel parentInstance;
    public Map<String, String> textures;
    public List<Texture> textureInstances;
    public Cube[] cubes;
    public boolean[] fullFaces;

    public static BlockModel deserialize(AssetURL url, JsonElement json) {
        var object = json.getAsJsonObject();
        var data = new BlockModel();
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

    @Override
    public BakedModel bake() {
        return null;
    }

    @Override
    public List<AssetURL> getTextures() {
        return null;
    }
}
