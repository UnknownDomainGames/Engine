package engine.mod.metadata;

import com.google.gson.*;
import engine.mod.Dependency;

import java.lang.reflect.Type;

public class DependencyPersistence implements JsonSerializer<Dependency>, JsonDeserializer<Dependency> {
    @Override
    public JsonElement serialize(Dependency src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Dependency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Dependency.parse(json.getAsString());
    }
}
