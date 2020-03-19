package engine.mod.metadata;

import com.google.gson.*;
import engine.util.versioning.Version;

import java.lang.reflect.Type;

public final class VersionPersistence implements JsonSerializer<Version>, JsonDeserializer<Version> {
    @Override
    public JsonElement serialize(Version src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Version(json.getAsString());
    }
}
