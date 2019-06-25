package nullengine.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class JsonUtils {

    public static final JsonParser DEFAULT_JSON_PARSER = new JsonParser();

    private static final Gson GSON = new Gson();

    public static Gson gson() {
        return GSON;
    }
}
