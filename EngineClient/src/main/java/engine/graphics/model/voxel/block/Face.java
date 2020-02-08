package engine.graphics.model.voxel.block;

import com.google.gson.JsonElement;
import engine.client.asset.AssetURL;

import java.util.Set;

import static engine.graphics.model.voxel.ModelJsonUtils.cullFaces;

final class Face {

    Texture texture;
    int cullFaces;

    static Face deserialize(BlockModel blockModel, JsonElement json, Set<AssetURL> requestTextures) {
        if (json == null) {
            return null;
        }

        var object = json.getAsJsonObject();
        var face = new Face();
        face.texture = Texture.deserialize(blockModel, json, requestTextures);
        face.cullFaces = cullFaces(object.get("CullFaces"));
        return face;
    }
}
