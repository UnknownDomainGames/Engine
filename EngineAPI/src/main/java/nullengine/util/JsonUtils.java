package nullengine.util;

import com.google.gson.Gson;
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
}
