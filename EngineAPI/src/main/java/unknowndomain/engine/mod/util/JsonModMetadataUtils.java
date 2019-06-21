package unknowndomain.engine.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.misc.DefaultModMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static ModMetadata fromJson(JsonObject jo) {
        DefaultModMetadata.Builder builder = DefaultModMetadata.builder();

        if (jo.has("id")) {
            builder.id(jo.get("id").getAsString());
        }

        if (jo.has("mainClass")) {
            builder.mainClass(jo.get("mainClass").getAsString());
        }

        if (jo.has("version")) {
            builder.version(jo.get("version").getAsString());
        }
        if (jo.has("name")) {
            builder.name(jo.get("name").getAsString());
        }
        if (jo.has("description")) {
            builder.description(jo.get("description").getAsString());
        }
        if (jo.has("license")) {
            builder.license(jo.get("license").getAsString());
        }
        if (jo.has("url")) {
            builder.url(jo.get("url").getAsString());
        }
        if (jo.has("logo")) {
            builder.logo(jo.get("logo").getAsString());
        }
        if (jo.has("authors")) {
            List<String> authors = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("authors")) {
                if (je.isJsonPrimitive()) {
                    authors.add(je.getAsString());
                }
            }
            builder.authors(List.copyOf(authors));
        }
        if (jo.has("dependencies")) {
            List<ModDependencyEntry> dependencies = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("dependencies")) {
                dependencies.add(ModDependencyEntry.parse(je.getAsString()));
            }
            builder.dependencies(List.copyOf(dependencies));
        }
        if (jo.has("properties")) {
            Map<String, String> properties = new HashMap<>();
            JsonObject jProperties = jo.getAsJsonObject("properties");
            for (Map.Entry<String, JsonElement> entry : jProperties.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().getAsString());
            }
            builder.properties(Map.copyOf(properties));
        }
        return builder.build();
    }
}
