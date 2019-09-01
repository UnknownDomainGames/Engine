package nullengine.client.rendering.model.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nullengine.util.Direction;

import java.lang.reflect.Type;

import static nullengine.client.rendering.model.ModelUtils.toDirectionInt;

public enum CullFacesDeserializer implements JsonDeserializer<Integer> {

    INSTANCE;

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return toDirectionInt(Direction.valueOf(json.getAsString().toUpperCase()));
        } else if (json.isJsonArray()) {
            var array = json.getAsJsonArray();
            int result = 0;
            for (var element : array) {
                result |= toDirectionInt(Direction.valueOf(element.getAsString().toUpperCase()));
            }
            return result;
        }
        throw new JsonParseException("Illegal cull faces");
    }
}
