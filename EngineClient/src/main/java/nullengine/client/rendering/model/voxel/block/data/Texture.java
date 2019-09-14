package nullengine.client.rendering.model.voxel.block.data;

import com.google.gson.JsonElement;
import nullengine.client.asset.AssetURL;
import org.joml.Vector4f;

import java.util.Set;

class Texture {

    private static final Vector4f DEFAULT_UV = new Vector4f(0, 0, 1, 1);

    AssetURL name;
    Vector4f uv;

    static Texture deserialize(BlockModel blockModel, JsonElement json, Set<AssetURL> requestTextures) {
        var object = json.getAsJsonObject();
        var texture = new Texture();
        texture.name = AssetURL.fromString(blockModel.url, object.get("texture").getAsString());
        requestTextures.add(texture.name);
        texture.uv = ModelJsonUtils.vector4f(object.get("uv"), DEFAULT_UV);
        return texture;
    }
}
