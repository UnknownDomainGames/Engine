package engine.graphics.model.voxel.block;

import com.google.gson.JsonElement;
import engine.client.asset.AssetURL;
import engine.graphics.model.voxel.ModelJsonUtils;
import org.joml.Vector4f;

import java.util.Set;

class Texture {

    private static final Vector4f DEFAULT_UV = new Vector4f(0, 0, 1, 1);

    AssetURL name;
    Vector4f uv;

    static Texture deserialize(BlockModel blockModel, JsonElement json, Set<AssetURL> requestTextures) {
        var object = json.getAsJsonObject();
        var texture = new Texture();
        texture.name = AssetURL.fromString(blockModel.url, object.get("Texture").getAsString());
        requestTextures.add(texture.name);
        texture.uv = ModelJsonUtils.vector4f(object.get("UV"), DEFAULT_UV);
        return texture;
    }
}
