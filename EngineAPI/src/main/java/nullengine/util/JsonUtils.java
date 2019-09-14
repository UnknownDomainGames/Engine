package nullengine.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils {

    private static final JsonParser DEFAULT_JSON_PARSER = new JsonParser();
    private static final Gson GSON = new Gson();

    public static JsonParser parser() {
        return DEFAULT_JSON_PARSER;
    }

    public static Gson gson() {
        return GSON;
    }

    public static String getAsStringOrNull(JsonElement json) {
        return getAsStringOrDefault(json, null);
    }

    public static String getAsStringOrDefault(JsonElement json, String defaultValue) {
        if (json == null) return defaultValue;
        if (!json.isJsonPrimitive()) return defaultValue;
        if (!json.getAsJsonPrimitive().isString()) return defaultValue;
        return json.getAsString();
    }

    public static Number getAsNumberOrNull(JsonElement json) {
        if (json == null) return null;
        if (!json.isJsonPrimitive()) return null;
        if (!json.getAsJsonPrimitive().isNumber()) return null;
        return json.getAsNumber();
    }

    public static boolean getAsBooleanOrDefault(JsonElement json, boolean defaultValue) {
        if (json == null) return defaultValue;
        if (!json.isJsonPrimitive()) return defaultValue;
        if (!json.getAsJsonPrimitive().isString()) return defaultValue;
        return json.getAsBoolean();
    }
}
