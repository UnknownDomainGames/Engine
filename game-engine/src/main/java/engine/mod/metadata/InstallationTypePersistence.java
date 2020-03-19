package engine.mod.metadata;

import com.google.gson.*;
import engine.mod.InstallationType;

import java.lang.reflect.Type;

public class InstallationTypePersistence implements JsonSerializer<InstallationType>, JsonDeserializer<InstallationType> {
    @Override
    public JsonElement serialize(InstallationType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }

    @Override
    public InstallationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return InstallationType.valueOf(json.getAsString().toUpperCase());
    }
}
