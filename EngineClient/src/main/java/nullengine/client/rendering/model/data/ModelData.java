package nullengine.client.rendering.model.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.TextureAtlasPart;
import nullengine.util.Direction;

import java.lang.reflect.Type;
import java.util.Map;

public final class ModelData {

    public transient AssetURL url;
    public String parent;
    public transient ModelData parentInstance;
    public Map<String, String> textures;
    public transient Map<String, TextureAtlasPart> textureInstance;
    public Cube[] cubes;
    public boolean[] fullFaces;

    public enum Deserializer implements JsonDeserializer<ModelData> {
        INSTANCE;

        private static final Type TEXTURES_TYPE = new TypeToken<Map<String, String>>() {
        }.getType();

        @Override
        public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var object = json.getAsJsonObject();
            var data = new ModelData();
            data.parent = context.deserialize(object.get("parent"), String.class);
            data.textures = context.deserialize(object.get("textures"), TEXTURES_TYPE);
            data.cubes = context.deserialize(object.get("cubes"), Cube[].class);
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
}
