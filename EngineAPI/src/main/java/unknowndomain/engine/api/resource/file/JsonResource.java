package unknowndomain.engine.api.resource.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import unknowndomain.engine.api.Platform;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonResource extends FileResource {
    private static Gson gson;

    public JsonResource(Path path) {
        super(path);
    }

    public JsonObject toJsonObject() {
        try {
            return gson.fromJson(Files.newBufferedReader(getJPath()), JsonObject.class);
        } catch (IOException e) {
            Platform.getLogger().error("Cannot read JSON", e);
            return new JsonObject();
        }
    }

    public <T> T deserialize(Type type) {
        try {
            return gson.fromJson(Files.newBufferedReader(getJPath()), type);
        } catch (IOException e) {
            Platform.getLogger().error("Cannot read JSON", e);
            return null;
        }
    }

    public LanguageResource toLanguageResource() {
        return getPath().toString().contains("lang/") ? new LanguageResource(getJPath()) : null;
    }
}
