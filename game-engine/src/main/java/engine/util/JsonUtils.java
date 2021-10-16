package engine.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public final class JsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

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

    private JsonUtils() {
    }
}
