package unknowndomain.engine.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModMetadata;

import java.util.Map;

public class JsonModMetadataUtils {

    public static JsonObject toJson(ModMetadata descriptor) {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", descriptor.getId());
        jo.addProperty("version", descriptor.getVersion().toString());
        jo.addProperty("mainClass", descriptor.getMainClass());
        jo.addProperty("name", descriptor.getName());
        jo.addProperty("description", descriptor.getDescription());
        jo.addProperty("license", descriptor.getLicense());
        jo.addProperty("url", descriptor.getUrl());
        jo.addProperty("logo", descriptor.getLogoFile());

        JsonArray ja = new JsonArray();
        for (String author : descriptor.getAuthors()) {
            ja.add(author);
        }
        jo.add("authors", ja);

        ja = new JsonArray();
        for (ModDependencyEntry dependencyEntry : descriptor.getDependencies()) {
            ja.add(dependencyEntry.toString());
        }
        jo.add("dependencies", ja);

        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> entry : descriptor.getProperties().entrySet()) {
            properties.addProperty(entry.getKey(), entry.getValue());
        }
        jo.add("properties", properties);
        return jo;
    }
}
