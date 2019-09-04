package nullengine.client.rendering.model.data;

import com.google.gson.JsonElement;
import nullengine.client.rendering.texture.TextureAtlasPart;
import org.joml.Vector4f;

public class Texture {

    public String name;
    public transient TextureAtlasPart instance;
    public Vector4f uv;

    public static Texture deserialize(JsonElement json) {
        var object = json.getAsJsonObject();
        var texture = new Texture();
        texture.name = object.get("texture").getAsString();
        texture.uv = ModelJsonUtils.vector4f(object.get("uv"));
        return texture;
    }
}
