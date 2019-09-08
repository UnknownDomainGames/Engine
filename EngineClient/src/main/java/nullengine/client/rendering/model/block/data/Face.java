package nullengine.client.rendering.model.block.data;

import com.google.gson.JsonElement;

public final class Face {

    public Texture texture;
    public int cullFaces;

    public static Face deserialize(BlockModel blockModel, JsonElement json) {
        if (json == null) {
            return null;
        }

        var object = json.getAsJsonObject();
        var face = new Face();
        face.texture = Texture.deserialize(json);
        blockModel.textureInstances.add(face.texture);
        face.cullFaces = ModelJsonUtils.cullFaces(object.get("cullFaces"));
        return face;
    }
}
